package net.plshark.usererror.authorization

interface AuthorizationService {

    /**
     * Validate an access token
     * @param accessToken the access token
     * @return the username and authorities
     * @throws BadCredentialsException if the token is invalid
     */
    suspend fun validateToken(accessToken: String): AuthenticatedUser
}
