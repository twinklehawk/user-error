package net.plshark.usererror.server.authentication.token

import kotlinx.coroutines.reactive.awaitFirstOrNull
import net.plshark.usererror.authentication.AccountCredentials
import net.plshark.usererror.authentication.token.AuthToken
import net.plshark.usererror.authentication.token.TokenAuthenticationService
import net.plshark.usererror.user.UserTokenSettings
import net.plshark.usererror.user.UserTokenSettingsService
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authentication/token")
class TokenAuthenticationController(
    private val passwordEncoder: PasswordEncoder,
    private val userDetailsService: ReactiveUserDetailsService,
    private val tokenVerifier: TokenVerifier,
    private val tokenBuilder: TokenBuilder,
    private val userTokenSettingsService: UserTokenSettingsService
) : TokenAuthenticationService {

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    override suspend fun authenticate(credentials: AccountCredentials): AuthToken {
        val userDetails = userDetailsService.findByUsername(credentials.username)
            .filter { passwordEncoder.matches(credentials.password, it.password) }
            .awaitFirstOrNull() ?: throw BadCredentialsException("Invalid credentials")
        return buildAuthToken(userDetails)
    }

    @PostMapping(
        value = ["/refresh"],
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    override suspend fun refresh(refreshToken: String): AuthToken {
        val username = tokenVerifier.verifyRefreshToken(refreshToken)
        val userDetails = userDetailsService.findByUsername(username).awaitFirstOrNull()
            ?: throw BadCredentialsException("Invalid credentials")
        return buildAuthToken(userDetails)
    }

    private suspend fun buildAuthToken(user: UserDetails): AuthToken {
        val settings = userTokenSettingsService.findByUsername(user.username)
        return buildAuthToken(user, settings)
    }

    private fun buildAuthToken(user: UserDetails, settings: UserTokenSettings): AuthToken {
        val tokenExpiration = settings.authTokenExpiration ?: userTokenSettingsService.getDefaultTokenExpiration()
        val authorities = user.authorities.map { obj: GrantedAuthority -> obj.authority }.toTypedArray()
        var refreshToken: String? = null
        if (settings.refreshTokenEnabled) {
            val refreshExpiration = settings.refreshTokenExpiration
                ?: userTokenSettingsService.getDefaultTokenExpiration()
            refreshToken = tokenBuilder.buildRefreshToken(user.username, refreshExpiration)
        }

        return AuthToken(
            accessToken = tokenBuilder.buildAccessToken(user.username, tokenExpiration, authorities),
            expiresIn = tokenExpiration / MILLISECONDS_IN_SECONDS,
            refreshToken = refreshToken,
            scope = null
        )
    }

    companion object {
        private const val MILLISECONDS_IN_SECONDS = 1000
    }
}
