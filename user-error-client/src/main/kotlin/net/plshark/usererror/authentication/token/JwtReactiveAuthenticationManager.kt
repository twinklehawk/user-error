package net.plshark.usererror.authentication.token

import kotlinx.coroutines.reactor.mono
import net.plshark.usererror.authorization.AuthenticatedUser
import net.plshark.usererror.authorization.AuthorizationService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import reactor.core.publisher.Mono

/**
 * Authentication manager that validates JWT authentication
 */
class JwtReactiveAuthenticationManager(private val authService: AuthorizationService) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        return Mono.fromCallable { extractToken(authentication) }
            .flatMap { verifyToken(it) }
            .map { (username, authorities) ->
                JwtAuthenticationToken(
                    username = username,
                    authorities = authorities.map { SimpleGrantedAuthority(it) }.toSet(),
                    authenticated = true,
                    token = null
                )
            }
    }

    private fun extractToken(authentication: Authentication?): String {
        if (authentication !is JwtAuthenticationToken || authentication.credentials == null)
            throw BadCredentialsException("Invalid credentials")
        return authentication.credentials!!
    }

    /**
     * Verify and decode a JWT
     * @param token the JWT
     * @return user info from the JWT or BadCredentialsException if the token is invalid
     */
    private fun verifyToken(token: String): Mono<AuthenticatedUser> {
        return mono { authService.validateToken(token) }
    }
}
