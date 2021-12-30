package net.plshark.usererror.authentication.token.spring

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority

class JwtAuthenticationTokenTest {

    @Test
    fun `builder should create correct token`() {
        val token = JwtAuthenticationToken(
            username = "username",
            token = "test-token",
            authenticated = true,
            authorities = setOf(SimpleGrantedAuthority("auth1"), SimpleGrantedAuthority("auth2"))
        )

        assertEquals("username", token.name)
        assertEquals("username", token.principal)
        assertEquals("test-token", token.credentials)
        assertTrue(token.authenticated)
        assertEquals(2, token.authorities.size)
        assertTrue(token.authorities.contains(SimpleGrantedAuthority("auth1")))
        assertTrue(token.authorities.contains(SimpleGrantedAuthority("auth2")))
    }
}
