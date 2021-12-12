package net.plshark.usererror.authentication.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class DefaultTokenBuilderTest {

    private val algorithm = Algorithm.HMAC256("test-secret")
    private val issuer = "test-issuer"
    private val builder = DefaultTokenBuilder(algorithm, issuer)

    @Test
    fun `access tokens should include the username, issuer, authorities, and expiration`() {
        val token = builder.buildAccessToken("test-user", 1000, arrayOf("role1", "role2"))
        val decodedToken = JWT.decode(token)

        assertEquals("test-user", decodedToken.subject)
        assertNotNull(decodedToken.expiresAt)
        assertEquals("test-issuer", decodedToken.issuer)
        assertEquals(2, decodedToken.getClaim(PlsharkClaim.AUTHORITIES).asList(String::class.java).size)
    }

    @Test
    fun `refresh tokens should include the username, issuer, refresh claim, and expiration`() {
        val token = builder.buildRefreshToken("test-user", 1000)
        val decodedToken = JWT.decode(token)

        assertEquals("test-user", decodedToken.subject)
        assertNotNull(decodedToken.expiresAt)
        assertEquals("test-issuer", decodedToken.issuer)
        assertFalse(decodedToken.getClaim(PlsharkClaim.REFRESH).isNull)
    }
}
