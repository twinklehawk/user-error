package net.plshark.users.auth.service

import kotlinx.coroutines.reactive.awaitFirstOrNull
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

    override suspend fun authenticate(credentials: AccountCredentials): AuthToken {
        val userDetails = userDetailsService.findByUsername(credentials.username)
            .filter { passwordEncoder.matches(credentials.password, it.password) }
            .awaitFirstOrNull() ?: throw BadCredentialsException("Invalid credentials")
        return buildAuthToken(userDetails)
    }

    override suspend fun refresh(refreshToken: String): AuthToken {
        val username = tokenVerifier.verifyRefreshToken(refreshToken)
        val userDetails = userDetailsService.findByUsername(username).awaitFirstOrNull()
            ?: throw BadCredentialsException("Invalid credentials")
        return buildAuthToken(userDetails)
    }

    override suspend fun validateToken(accessToken: String): AuthenticatedUser {
        return tokenVerifier.verifyToken(accessToken)
    }

    private suspend fun buildAuthToken(user: UserDetails): AuthToken {
        val settings = userAuthSettingsService.findByUsername(user.username)
        return buildAuthToken(user, settings)
    }

    private fun buildAuthToken(user: UserDetails, settings: UserAuthSettings): AuthToken {
        val tokenExpiration = settings.authTokenExpiration ?: userAuthSettingsService.getDefaultTokenExpiration()
        val authorities = user.authorities.map { obj: GrantedAuthority -> obj.authority }.toTypedArray()
        var refreshToken: String? = null
        if (settings.refreshTokenEnabled) {
            val refreshExpiration = settings.refreshTokenExpiration
                ?: userAuthSettingsService.getDefaultTokenExpiration()
            refreshToken = tokenBuilder.buildRefreshToken(user.username, refreshExpiration)
        }

        return AuthToken(
            accessToken = tokenBuilder.buildAccessToken(user.username, tokenExpiration, authorities),
            expiresIn = tokenExpiration / MILLISECONDS_IN_SECONDS,
            refreshToken = refreshToken,
            scope = null
        )
    }
}

private const val MILLISECONDS_IN_SECONDS = 1000
