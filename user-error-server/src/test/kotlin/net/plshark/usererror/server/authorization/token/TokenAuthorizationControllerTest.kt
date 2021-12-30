package net.plshark.usererror.server.authorization.token

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.authorization.UserAuthorities
import net.plshark.usererror.server.authentication.token.TokenVerifier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.BadCredentialsException

class TokenAuthorizationControllerTest {

    private val tokenVerifier = mockk<TokenVerifier>()
    private val service = TokenAuthorizationController(tokenVerifier)

    @Test
    fun `validate should complete successfully for a valid token`() = runBlocking {
        val user = UserAuthorities(
            username = "test-user",
            authorities = setOf()
        )
        every { tokenVerifier.verifyToken("access-token") } returns user

        assertEquals(user, service.getAuthoritiesForToken("access-token"))
    }

    @Test
    fun `validate should return an exception for an invalid token`() {
        every { tokenVerifier.verifyToken("access-token") } throws BadCredentialsException("test exception")

        assertThrows<BadCredentialsException> {
            runBlocking {
                service.getAuthoritiesForToken("access-token")
            }
        }
    }
}
