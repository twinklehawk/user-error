package net.plshark.usererror.server.authentication.token

import net.plshark.usererror.authorization.UserAuthorities

/**
 * Verifies JWTs
 */
interface TokenVerifier {

    /**
     * Verify an access token
     * @param token the token
     * @return the username and authorities of the corresponding user if successful
     * @throws BadCredentialsException if the token is invalid or expired
     */
    fun verifyToken(token: String): UserAuthorities

    /**
     * Verify a refresh token
     * @param token the token to verify
     * @return the username if successful
     * @throws BadCredentialsException if the token is invalid or expired
     */
    fun verifyRefreshToken(token: String): String
}
