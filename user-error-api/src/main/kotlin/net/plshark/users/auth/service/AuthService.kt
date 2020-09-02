package net.plshark.users.auth.service

import net.plshark.users.auth.model.AccountCredentials
import net.plshark.users.auth.model.AuthToken
import net.plshark.users.auth.model.AuthenticatedUser
import reactor.core.publisher.Mono

/**
 * Service to authenticate a user using tokens
 */
interface AuthService {

    /**
     * Authenticate a user and generate an auth token
     * @param credentials the user credentials
     * @return a [Mono] emitting the auth token if successful or a BadCredentialsException if the credentials are
     * invalid
     */
    fun authenticate(credentials: AccountCredentials): Mono<AuthToken>

    /**
     * Refresh an existing token
     * @param refreshToken the existing token
     * @return a [Mono] emitting a new auth token or a BadCredentialsException if the refresh token is invalid
     */
    fun refresh(refreshToken: String): Mono<AuthToken>

    /**
     * Validate an access token
     * @param accessToken the access token
     * @return a [Mono] emitting the username and authorities if the token is valid or a BadCredentialsException if not
     */
    fun validateToken(accessToken: String): Mono<AuthenticatedUser>
}
