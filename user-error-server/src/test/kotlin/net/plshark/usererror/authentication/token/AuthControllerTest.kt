package net.plshark.usererror.authentication.token

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.authentication.AccountCredentials
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AuthControllerTest {

    private val service = mockk<AuthenticationService>()
    private val controller = AuthController(service)

    @Test
    fun `authenticate should pass the credentials through to the service`() = runBlocking {
        val token = AuthToken(
            accessToken = "access", tokenType = "type", expiresIn = 1, refreshToken = "refresh", scope = "scope"
        )
        coEvery { service.authenticate(AccountCredentials("test-user", "test-password")) } returns token

        assertEquals(token, controller.authenticate(AccountCredentials("test-user", "test-password")))
    }

    @Test
    fun `refresh should pass the token through to the service`() = runBlocking {
        val token = AuthToken(
            accessToken = "access", tokenType = "type", expiresIn = 1, refreshToken = "refresh", scope = "scope"
        )
        coEvery { service.refresh("test-token") } returns token

        assertEquals(token, controller.refresh("test-token"))
    }

    @Test
    fun `validateToken should pass the token through to the service`() = runBlocking {
        val user = AuthenticatedUser(username = "user", authorities = setOf())
        coEvery { service.validateToken("refresh") } returns user

        assertEquals(user, controller.validateToken("refresh"))
    }
}
