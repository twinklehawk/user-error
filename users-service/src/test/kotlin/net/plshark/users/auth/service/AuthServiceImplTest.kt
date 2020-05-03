package net.plshark.users.auth.service

import io.mockk.every
import io.mockk.mockk
import net.plshark.users.auth.model.AccountCredentials
import net.plshark.users.auth.model.AuthToken
import net.plshark.users.auth.model.AuthenticatedUser
import net.plshark.users.auth.model.UserAuthSettings
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class AuthServiceImplTest {

    private val passwordEncoder = mockk<PasswordEncoder>()
    private val userDetailsService = mockk<ReactiveUserDetailsService>()
    private val tokenVerifier = mockk<TokenVerifier>()
    private val tokenBuilder = mockk<TokenBuilder>()
    private val authSettingsService = mockk<UserAuthSettingsService>()
    private val service = AuthServiceImpl(passwordEncoder, userDetailsService, tokenVerifier, tokenBuilder, authSettingsService)
    private val settings = UserAuthSettings(id = null, userId = null, authTokenExpiration = 1000, refreshTokenExpiration = 1000)

    @Test
    fun `authenticate should build access and refresh tokens with the correct expiration`() {
        every { userDetailsService.findByUsername("test-user") } returns Mono.just(User("test-user", "encoded-password", listOf()))
        every { passwordEncoder.matches("test-password", "encoded-password") } returns true
        every { tokenBuilder.buildAccessToken("test-user", 1000L, arrayOf()) } returns "test-token"
        every { tokenBuilder.buildRefreshToken("test-user", 1000L) } returns "refresh-token"
        every { authSettingsService.findByUsername("test-user") } returns Mono.just(settings)
        every { authSettingsService.getDefaultTokenExpiration() } returns 5000

        StepVerifier.create(service.authenticate(AccountCredentials("test-user", "test-password")))
                .expectNext(AuthToken(accessToken = "test-token", expiresIn = 1, refreshToken = "refresh-token", scope = null))
                .verifyComplete()
    }

    @Test
    fun `authenticate should not return a refresh token if refresh is not enabled for the user`() {
        every { userDetailsService.findByUsername("test-user") } returns Mono.just(User("test-user", "encoded-password", listOf()))
        every { passwordEncoder.matches("test-password", "encoded-password") } returns true
        every { tokenBuilder.buildAccessToken("test-user", 1000L, arrayOf()) } returns "test-token"
        every { tokenBuilder.buildRefreshToken("test-user", 1000L) } returns "refresh-token"
        every { authSettingsService.findByUsername("test-user") } returns Mono.just(settings.copy(refreshTokenEnabled = false))
        every { authSettingsService.getDefaultTokenExpiration() } returns 5000

        StepVerifier.create(service.authenticate(AccountCredentials("test-user", "test-password")))
                .expectNext(AuthToken(accessToken = "test-token", expiresIn = 1, refreshToken = null, scope = null))
                .verifyComplete()
    }

    @Test
    fun `should build a token with default expiration time if no expiration time settings are set`() {
        every { userDetailsService.findByUsername("test-user") } returns Mono.just(User("test-user", "encoded-password", listOf()))
        every { passwordEncoder.matches("test-password", "encoded-password") } returns true
        every { tokenBuilder.buildAccessToken("test-user", 20000L, arrayOf()) } returns "test-token"
        every { tokenBuilder.buildRefreshToken("test-user", 20000L) } returns "refresh-token"
        every { authSettingsService.findByUsername("test-user") } returns Mono.just(UserAuthSettings(null, null, false, null, null))
        every { authSettingsService.getDefaultTokenExpiration() } returns 20000

        StepVerifier.create(service.authenticate(AccountCredentials("test-user", "test-password")))
                .expectNext(AuthToken(accessToken = "test-token", expiresIn = 20, refreshToken = null, scope = null))
                .verifyComplete()
    }

    @Test
    fun `authenticate should return an exception if no matching user is found`() {
        every { userDetailsService.findByUsername("test-user") } returns Mono.empty()

        StepVerifier.create(service.authenticate(AccountCredentials("test-user", "test-password")))
                .verifyError(BadCredentialsException::class.java)
    }

    @Test
    fun `authenticate should return an exception if the credentials are invalid`() {
        every { userDetailsService.findByUsername("test-user") } returns Mono.just(User("test-user", "encoded-password", listOf()))
        every { passwordEncoder.matches("test-password", "encoded-password") } returns false

        StepVerifier.create(service.authenticate(AccountCredentials("test-user", "test-password")))
                .verifyError(BadCredentialsException::class.java)
    }

    @Test
    fun `refresh should build access and refresh tokens with the correct expiration`() {
        every { tokenVerifier.verifyRefreshToken("refresh-token") } returns "test-user"
        every { userDetailsService.findByUsername("test-user") } returns Mono.just(User("test-user", "encoded-password", listOf()))
        every { tokenBuilder.buildAccessToken("test-user", 1000L, arrayOf()) } returns "test-token"
        every { tokenBuilder.buildRefreshToken("test-user", 1000L) } returns "refresh-token"
        every { authSettingsService.findByUsername("test-user") } returns Mono.just(settings)
        every { authSettingsService.getDefaultTokenExpiration() } returns 5000

        StepVerifier.create(service.refresh("refresh-token"))
            .expectNext(AuthToken(accessToken = "test-token", expiresIn = 1, refreshToken = "refresh-token", scope = null))
                .verifyComplete()
    }

    @Test
    fun `refresh should return an exception if the token is invalid`() {
        every { tokenVerifier.verifyRefreshToken("refresh-token") } throws BadCredentialsException("test exception")

        StepVerifier.create(service.refresh("refresh-token"))
                .verifyError(BadCredentialsException::class.java)
    }

    @Test
    fun `refresh should return an exception if the corresponding user is not found`() {
        every { tokenVerifier.verifyRefreshToken("refresh-token") } returns "test-user"
        every { userDetailsService.findByUsername("test-user") } returns Mono.empty()

        StepVerifier.create(service.refresh("refresh-token"))
                .verifyError(BadCredentialsException::class.java)
    }

    @Test
    fun `validate should complete successfully for a valid token`() {
        val user = AuthenticatedUser(
            username = "test-user",
            authorities = setOf()
        )
        every { tokenVerifier.verifyToken("access-token") } returns user

        StepVerifier.create(service.validateToken("access-token"))
                .expectNext(user)
                .verifyComplete()
    }

    @Test
    fun `validate should return an exception for an invalid token`() {
        every { tokenVerifier.verifyToken("access-token") } throws BadCredentialsException("test exception")

        StepVerifier.create(service.validateToken("access-token"))
                .verifyError(BadCredentialsException::class.java)
    }
}
