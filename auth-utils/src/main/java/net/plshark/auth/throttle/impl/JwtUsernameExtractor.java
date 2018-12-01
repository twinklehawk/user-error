package net.plshark.auth.throttle.impl;

import java.util.Objects;
import java.util.Optional;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Payload;
import net.plshark.auth.throttle.UsernameExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * Username extractor for requests using bearer authentication with JWT
 */
public class JwtUsernameExtractor implements UsernameExtractor {

    private static final String AUTHORIZATION_TYPE = "Bearer ";
    private static final Logger log = LoggerFactory.getLogger(JwtUsernameExtractor.class);

    private final JWTVerifier verifier;

    /**
     * Create a new instance
     * @param verifier the verifier to use to verify and decode JWTs
     */
    public JwtUsernameExtractor(JWTVerifier verifier) {
        this.verifier = Objects.requireNonNull(verifier, "verifier cannot be null");
    }

    @Override
    public Optional<String> extractUsername(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst("Authorization"))
                .flatMap(this::extractUsername);
    }

    /**
     * Extract the username from an authorization header value
     * @param header the header value, preferably a valid JWT
     * @return the username
     */
    private Optional<String> extractUsername(String header) {
        try {
            return Optional.of(header)
                    .filter(str -> str.startsWith(AUTHORIZATION_TYPE))
                    .map(str -> verifier.verify(str.substring(AUTHORIZATION_TYPE.length())))
                    .map(Payload::getSubject);
        } catch (JWTVerificationException e) {
            log.debug("Invalid JWT token", e);
            return Optional.empty();
        }
    }
}
