package net.plshark.users.auth.jwt

import net.plshark.users.auth.model.AuthenticatedUser
import net.plshark.users.auth.service.AuthService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

/**
 * Authentication manager that validates JWT authentication
 */
class JwtReactiveAuthenticationManager(private val authService: AuthService) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        // TODO authentication can be null?
        return Mono.just(authentication)
            .filter { auth: Authentication? -> auth is JwtAuthenticationToken }
            .map { auth: Authentication -> auth as JwtAuthenticationToken }
            // TODO credentials can be null?
            .map { token: JwtAuthenticationToken -> token.credentials }
            .publishOn(Schedulers.parallel())
            .flatMap { token: String? -> verifyToken(token!!) }
            .switchIfEmpty(Mono.error { BadCredentialsException("Invalid credentials") })
            .map { (username, authorities) ->
                JwtAuthenticationToken(
                    username = username,
                    authorities = authorities.map { SimpleGrantedAuthority(it) }.toSet(),
                    authenticated = true,
                    token = null
                )
            }
    }

    /**
     * Verify and decode a JWT
     * @param token the JWT
     * @return user info from the JWT or BadCredentialsException if the token is invalid
     */
    private fun verifyToken(token: String): Mono<AuthenticatedUser> {
        return authService.validateToken(token)
    }
}