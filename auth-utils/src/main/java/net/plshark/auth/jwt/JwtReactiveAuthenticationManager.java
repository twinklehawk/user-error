package net.plshark.auth.jwt;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Authentication manager that validates JWT authentication
 */
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    public static final String AUTHORITIES_CLAIM = "https://users.plshark.net/authorities";

    private final JWTVerifier verifier;

    /**
     * Create a new instance
     * @param verifier the verifier to use to validate and parse JWTs
     */
    public JwtReactiveAuthenticationManager(JWTVerifier verifier) {
        this.verifier = verifier;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .map(auth -> (JwtAuthenticationToken) auth)
                .map(JwtAuthenticationToken::getCredentials)
                .publishOn(Schedulers.parallel())
                .map(this::verifyToken)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid credentials"))))
                .map(token -> JwtAuthenticationToken.builder()
                        .withUsername(token.getSubject())
                        .withAuthorities(parseAuthorities(token))
                        .withAuthenticated(true)
                        .build());
    }

    /**
     * Verify and decode a JWT
     * @param token the JWT
     * @return the decoded JWT
     * @throws BadCredentialsException if the token is invalid
     */
    private DecodedJWT verifyToken(String token) {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new BadCredentialsException("Invalid token", e);
        }
    }

    /**
     * Parse a list of authorities from a decoded JWT
     * @param jwt the decoded JWT
     * @return the list of authorities from the token
     */
    private List<String> parseAuthorities(DecodedJWT jwt) {
        return Optional.ofNullable(jwt.getClaim(AUTHORITIES_CLAIM).asList(String.class))
                .orElse(Collections.emptyList());
    }
}
