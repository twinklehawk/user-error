package net.plshark.users.auth.service;

/**
 * Creates auth and refresh JWTs
 */
public interface TokenBuilder {

    /**
     * Build an access token
     * @param username the user the token is for
     * @param expirationMs the number of milliseconds from now until the token should expire
     * @param authorities the user's authorities
     * @return the access token
     */
    String buildAccessToken(String username, long expirationMs, String[] authorities);

    /**
     * Build a refresh token
     * @param username the user the token is for
     * @param expirationMs the number of milliseconds from now until the token should expire
     * @return the refresh token
     */
    String buildRefreshToken(String username, long expirationMs);
}
