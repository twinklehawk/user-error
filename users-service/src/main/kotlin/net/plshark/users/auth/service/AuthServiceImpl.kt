package net.plshark.users.auth.service

import net.plshark.users.auth.model.AccountCredentials
import net.plshark.users.auth.model.AuthToken
import net.plshark.users.auth.model.AuthenticatedUser
import net.plshark.users.auth.model.UserAuthSettings
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

/**
 * Default AuthService server side implementation
 */
@Component
class AuthServiceImpl(
    private val passwordEncoder: PasswordEncoder,
    private val userDetailsService: ReactiveUserDetailsService,
    private val tokenVerifier: TokenVerifier,
    private val tokenBuilder: TokenBuilder,
    private val userAuthSettingsService: UserAuthSettingsService
) : AuthService {

    override fun authenticate(credentials: AccountCredentials): Mono<AuthToken> {
        val username = credentials.username
        return userDetailsService.findByUsername(username)
            .publishOn(Schedulers.parallel())
            .filter { user: UserDetails -> passwordEncoder.matches(credentials.password, user.password) }
            .switchIfEmpty(Mono.error { BadCredentialsException("Invalid Credentials") })
            .flatMap { user: UserDetails -> buildAuthToken(user) }
    }

    override fun refresh(refreshToken: String): Mono<AuthToken> {
        return Mono.just(refreshToken)
            .publishOn(Schedulers.parallel())
            .map { token: String -> tokenVerifier.verifyRefreshToken(token) }
            .flatMap { username: String -> userDetailsService.findByUsername(username) }
            .switchIfEmpty(Mono.error { BadCredentialsException("Invalid Credentials") })
            // TODO run any checks to see if user is allowed to refresh
            .flatMap { user: UserDetails -> buildAuthToken(user) }
    }

    override fun validateToken(accessToken: String): Mono<AuthenticatedUser> {
        return Mono.just(accessToken)
            .publishOn(Schedulers.parallel())
            .map { token: String -> tokenVerifier.verifyToken(token) }
    }

    private fun buildAuthToken(user: UserDetails): Mono<AuthToken> {
        return userAuthSettingsService.findByUsername(user.username)
            .map { settings: UserAuthSettings -> buildAuthToken(user, settings) }
    }

    private fun buildAuthToken(user: UserDetails, settings: UserAuthSettings): AuthToken {
        val tokenExpiration = Optional.ofNullable(settings.authTokenExpiration)
            .orElse(userAuthSettingsService.getDefaultTokenExpiration())
        val authorities = user.authorities.map { obj: GrantedAuthority -> obj.authority }.toTypedArray()
        var refreshToken: String? = null
        if (settings.refreshTokenEnabled) {
            val refreshExpiration = Optional.ofNullable(settings.refreshTokenExpiration)
                .orElse(userAuthSettingsService.getDefaultTokenExpiration())
            refreshToken = tokenBuilder.buildRefreshToken(user.username, refreshExpiration)
        }

        return AuthToken(
            accessToken = tokenBuilder.buildAccessToken(user.username, tokenExpiration, authorities),
            expiresIn = tokenExpiration / 1000,
            refreshToken = refreshToken,
            scope = null
        )
    }
}