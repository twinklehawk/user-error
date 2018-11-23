package net.plshark.auth.service;

import reactor.core.publisher.Mono;

/**
 * Verifies JWTs
 */
public interface TokenVerifier {

    /**
     * Verify an access token
     * @param token the token
     * @return the username of the corresponding user if successful or a BadCredentialsException if the token is invalid
     * or expired
     */
    Mono<String> verifyToken(String token);

    /**
     * Verify a refresh token
     * @param token the token to verify
     * @return the username if successful or a BadCredentialsException if the token is invalid or expired
     */
    Mono<String> verifyRefreshToken(String token);
}
