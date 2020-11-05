package net.plshark.users.auth.webservice

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.plshark.users.auth.model.AccountCredentials
import net.plshark.users.auth.model.AuthToken
import net.plshark.users.auth.model.AuthenticatedUser
import net.plshark.users.auth.service.AuthService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AuthControllerTest {

    private val service = mockk<AuthService>()
    private val controller = AuthController(service)

    @Test
    fun `authenticate should pass the credentials through to the service`() = runBlocking {
        val token = AuthToken(
            accessToken = "access",
            tokenType = "type",
            expiresIn = 1,
            refreshToken = "refresh",
            scope = "scope"
        )
        coEvery { service.authenticate(AccountCredentials("test-user", "test-password")) } returns
                token

        assertEquals(token, controller.authenticate(AccountCredentials("test-user", "test-password")))
    }

    @Test
    fun `refresh should pass the token through to the service`() = runBlocking {
        val token = AuthToken(
            accessToken = "access",
            tokenType = "type",
            expiresIn = 1,
            refreshToken = "refresh",
            scope = "scope"
        )
        coEvery { service.refresh("test-token") } returns token

        assertEquals(token, controller.refresh("test-token"))
    }

    @Test
    fun `validateToken should pass the token through to the service`() = runBlocking {
        coEvery { service.validateToken("refresh") } returns
                AuthenticatedUser(username = "user", authorities = setOf())

        assertEquals(AuthenticatedUser(username = "user", authorities = setOf()),
            controller.validateToken("refresh"))
    }
}
