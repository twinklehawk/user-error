package net.plshark.users.auth.service

import net.plshark.users.auth.model.AccountCredentials
import net.plshark.users.auth.model.AuthToken
import net.plshark.users.auth.model.AuthenticatedUser

/**
 * Service to authenticate a user using tokens
 */
interface AuthService {

    // TODO exceptions
    // TODO remove service layer

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

    /**
     * Validate an access token
     * @param accessToken the access token
     * @return the username and authorities
     * @throws BadCredentialsException if the if the token is invalid
     */
    suspend fun validateToken(accessToken: String): AuthenticatedUser
}
