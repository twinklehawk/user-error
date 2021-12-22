package net.plshark.usererror.authentication.token

/**
 * Creates auth and refresh JWTs
 */
interface TokenBuilder {
    /**
     * Build an access token
     * @param username the user the token is for
     * @param expirationMs the number of milliseconds from now until the token should expire
     * @param authorities the user's authorities
     * @return the access token
     */
    fun buildAccessToken(username: String, expirationMs: Long, authorities: Array<String>): String

    /**
     * Build a refresh token
     * @param username the user the token is for
     * @param expirationMs the number of milliseconds from now until the token should expire
     * @return the refresh token
     */
    fun buildRefreshToken(username: String, expirationMs: Long): String
}
