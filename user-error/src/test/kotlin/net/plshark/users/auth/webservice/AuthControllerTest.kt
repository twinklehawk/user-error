package net.plshark.users.auth.webservice

import io.mockk.every
import io.mockk.mockk
import net.plshark.users.auth.model.AccountCredentials
import net.plshark.users.auth.model.AuthToken
import net.plshark.users.auth.model.AuthenticatedUser
import net.plshark.users.auth.service.AuthService
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class AuthControllerTest {

    private val service = mockk<AuthService>()
    private val controller = AuthController(service)

    @Test
    fun `authenticate should pass the credentials through to the service`() {
        val token = AuthToken(
            accessToken = "access",
            tokenType = "type",
            expiresIn = 1,
            refreshToken = "refresh",
            scope = "scope"
        )
        every { service.authenticate(AccountCredentials("test-user", "test-password")) } returns Mono.just(token)

        StepVerifier.create(controller.authenticate(AccountCredentials("test-user", "test-password")))
                .expectNext(token)
                .verifyComplete()
    }

    @Test
    fun `refresh should pass the token through to the service`() {
        val token = AuthToken(
            accessToken = "access",
            tokenType = "type",
            expiresIn = 1,
            refreshToken = "refresh",
            scope = "scope"
        )
        every { service.refresh("test-token") } returns Mono.just(token)

        StepVerifier.create(controller.refresh("test-token"))
                .expectNext(token)
                .verifyComplete()
    }

    @Test
    fun `validateToken should pass the token through to the service`() {
        every { service.validateToken("refresh") } returns Mono.just(AuthenticatedUser(username = "user", authorities = setOf()))

        StepVerifier.create(controller.validateToken("refresh"))
                .expectNext(AuthenticatedUser(username = "user", authorities = setOf()))
                .verifyComplete()
    }
}
