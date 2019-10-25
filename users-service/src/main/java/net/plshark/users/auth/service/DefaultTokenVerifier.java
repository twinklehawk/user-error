package net.plshark.users.auth.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Payload;
import net.plshark.users.auth.model.AuthenticatedUser;
import org.springframework.security.authentication.BadCredentialsException;
import reactor.core.publisher.Mono;

/**
 * Verifies JWTs
 */
public class DefaultTokenVerifier implements TokenVerifier{

    private final JWTVerifier verifier;

    /**
     * Create a new instance
     * @param verifier the verifier to use to verify and decode JWTs
     */
    public DefaultTokenVerifier(JWTVerifier verifier) {
        this.verifier = Objects.requireNonNull(verifier);
    }

    @Override
    public Mono<AuthenticatedUser> verifyToken(String token) {
        return decodeToken(token)
                .map(jwt -> AuthenticatedUser.create(jwt.getSubject(), parseAuthorities(jwt)));
    }

    @Override
    public Mono<String> verifyRefreshToken(String token) {
        return decodeToken(token)
                .filter(validToken -> Boolean.TRUE.equals(validToken.getClaim(AuthService.REFRESH_CLAIM).asBoolean()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Token is not a refresh token"))))
                .map(Payload::getSubject);
    }

    /**
     * Validate and decode a token
     * @param token the token
     * @return the decoded token if successful or a BadCredentialsException if the token is invalid
     */
    private Mono<DecodedJWT> decodeToken(String token) {
        return Mono.defer(() -> {
            try {
                return Mono.just(verifier.verify(token));
            } catch (JWTVerificationException e) {
                throw new BadCredentialsException("Invalid credentials", e);
            }
        });
    }

    /**
     * Parse a list of authorities from a decoded JWT
     * @param jwt the decoded JWT
     * @return the list of authorities from the token
     */
    private List<String> parseAuthorities(DecodedJWT jwt) {
        return Optional.ofNullable(jwt.getClaim(AuthService.AUTHORITIES_CLAIM).asList(String.class))
                .orElse(Collections.emptyList());
    }
}
