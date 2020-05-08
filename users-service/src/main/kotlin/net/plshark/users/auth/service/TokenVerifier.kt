package net.plshark.users.auth.service

import net.plshark.users.auth.model.AuthenticatedUser

/**
 * Verifies JWTs
 */
interface TokenVerifier {
    /**
     * Verify an access token
     * @param token the token
     * @return the username and authorities of the corresponding user if successful or a BadCredentialsException if the
     * token is invalid or expired
     */
    fun verifyToken(token: String): AuthenticatedUser

    /**
     * Verify a refresh token
     * @param token the token to verify
     * @return the username if successful or a BadCredentialsException if the token is invalid or expired
     */
    fun verifyRefreshToken(token: String): String
}