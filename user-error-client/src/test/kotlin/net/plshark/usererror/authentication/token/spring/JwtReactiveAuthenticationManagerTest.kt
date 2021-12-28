package net.plshark.usererror.authentication.token.spring

import io.mockk.coEvery
import io.mockk.mockk
import net.plshark.usererror.authorization.AuthenticatedUser
import net.plshark.usererror.authorization.AuthorizationService
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import reactor.test.StepVerifier

class JwtReactiveAuthenticationManagerTest {

    private val authService = mockk<AuthorizationService>()
    private val manager = JwtReactiveAuthenticationManager(authService)

    @Test
    fun `should parse the username and authorities from the token and set the authorized flag`() {
        val token = JwtAuthenticationToken(
            username = null,
            token = "test-token",
            authenticated = false,
            authorities = setOf()
        )
        coEvery { authService.validateToken("test-token") } returns AuthenticatedUser(
            username = "test-user",
            authorities = setOf("a", "b")
        )

        StepVerifier.create(manager.authenticate(token))
            .expectNext(
                JwtAuthenticationToken(
                    username = "test-user",
                    token = null,
                    authenticated = true,
                    authorities = setOf(SimpleGrantedAuthority("a"), SimpleGrantedAuthority("b"))
                )
            ).verifyComplete()
    }

    @Test
    fun `an invalid token should throw a BadCredentialsException`() {
        val token = JwtAuthenticationToken(
            username = null,
            token = "bad-token",
            authenticated = false,
            authorities = setOf()
        )
        coEvery { authService.validateToken("bad-token") } throws BadCredentialsException("bad")

        StepVerifier.create(manager.authenticate(token))
            .verifyError(BadCredentialsException::class.java)
    }

    @Test
    fun `a non-jwt authorization should throw a BadCredentialsException`() {
        val token = UsernamePasswordAuthenticationToken("user", "pass")

        StepVerifier.create(manager.authenticate(token))
            .verifyError(BadCredentialsException::class.java)
    }

    @Test
    fun `a null authentication should throw a BadCredentialsException`() {
        StepVerifier.create(manager.authenticate(null))
            .verifyError(BadCredentialsException::class.java)
    }

    @Test
    fun `null credentials inside the token should throw a BadCredentialsException`() {
        val token = JwtAuthenticationToken(
            username = null,
            token = null,
            authenticated = false,
            authorities = setOf()
        )

        StepVerifier.create(manager.authenticate(token))
            .verifyError(BadCredentialsException::class.java)
    }
}
