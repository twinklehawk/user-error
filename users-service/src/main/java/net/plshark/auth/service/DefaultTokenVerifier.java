package net.plshark.auth.service;

import java.util.Objects;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Payload;
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
    public Mono<String> verifyToken(String token) {
        return decodeToken(token)
                .map(Payload::getSubject);
    }

    @Override
    public Mono<String> verifyRefreshToken(String token) {
        return decodeToken(token)
                .filter(validToken -> Boolean.TRUE == validToken.getClaim(AuthService.REFRESH_CLAIM).asBoolean())
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
}
