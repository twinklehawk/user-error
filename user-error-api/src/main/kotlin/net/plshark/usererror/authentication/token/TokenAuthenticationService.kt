package net.plshark.usererror.authentication.token

import net.plshark.usererror.authentication.AccountCredentials

/**
 * Service to authenticate a user using tokens
 */
interface TokenAuthenticationService {

    // TODO exceptions

    /**
     * Authenticate a user and generate an auth token
     * @param credentials the user credentials
     * @return the auth token if successful
     * @throws BadCredentialsException if the credentials are invalid
     */
    suspend fun authenticate(credentials: AccountCredentials): AuthToken

    /**
     * Refresh an existing token
     * @param refreshToken the existing token
     * @return a new auth token
     * @throws BadCredentialsException if the refresh token is invalid
     */
    suspend fun refresh(refreshToken: String): AuthToken
}
