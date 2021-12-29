package net.plshark.usererror.authorization.token

import net.plshark.usererror.authorization.UserAuthorities

interface TokenAuthorizationService {

    // TODO exceptions

    /**
     * Get the authorities provided by a token
     * @param accessToken the access token
     * @return the username and authorities
     * @throws BadCredentialsException if the token is invalid
     */
    suspend fun getAuthoritiesForToken(accessToken: String): UserAuthorities
}
