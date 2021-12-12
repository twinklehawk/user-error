package net.plshark.usererror.authorization

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AuthorizationControllerTest {

    private val service = mockk<AuthorizationService>()
    private val controller = AuthorizationController(service)

    @Test
    fun `validateToken should pass the token through to the service`() = runBlocking {
        val user = AuthenticatedUser(username = "user", authorities = setOf())
        coEvery { service.validateToken("refresh") } returns user

        assertEquals(user, controller.validateToken("refresh"))
    }
}
