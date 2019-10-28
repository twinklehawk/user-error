package net.plshark.users.auth.service;

import net.plshark.users.auth.model.AuthenticatedUser;

/**
 * Verifies JWTs
 */
public interface TokenVerifier {

    /**
     * Verify an access token
     * @param token the token
     * @return the username and authorities of the corresponding user if successful or a BadCredentialsException if the
     * token is invalid or expired
     */
    AuthenticatedUser verifyToken(String token);

    /**
     * Verify a refresh token
     * @param token the token to verify
     * @return the username if successful or a BadCredentialsException if the token is invalid or expired
     */
    String verifyRefreshToken(String token);
}
