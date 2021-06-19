package net.plshark.users.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import net.plshark.users.auth.model.AuthenticatedUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.BadCredentialsException

class DefaultTokenVerifierTest {

    private val algorithm = Algorithm.HMAC256("test-key")
    private val jwtVerifier = JWT.require(algorithm).build()
    private val verifier = DefaultTokenVerifier(jwtVerifier)

    @Test
    fun `valid access tokens should return the username and authorities`() {
        val token = JWT.create().withSubject("test-user")
                .withArrayClaim(PlsharkClaim.AUTHORITIES, arrayOf("user")).sign(algorithm)

        assertEquals(AuthenticatedUser(username = "test-user", authorities = setOf("user")),
            verifier.verifyToken(token))
    }

    @Test
    fun `no authorities claim should build an empty authorities list`() {
        val token = JWT.create().withSubject("test-user").sign(algorithm)

        assertEquals(AuthenticatedUser(username = "test-user", authorities = setOf()), verifier.verifyToken(token))
    }

    @Test
    fun `invalid access tokens should throw a BadCredentialsException`() {
        assertThrows<BadCredentialsException> { verifier.verifyToken("bad-token") }
    }

    @Test
    fun `valid refresh tokens should return the username`() {
        val token = JWT.create().withSubject("test-user").withClaim(PlsharkClaim.REFRESH, true).sign(algorithm)

        assertEquals("test-user", verifier.verifyRefreshToken(token))
    }

    @Test
    fun `invalid refresh tokens should throw a BadCredentialsException`() {
        assertThrows<BadCredentialsException> { verifier.verifyRefreshToken("bad-token") }
    }

    @Test
    fun `refresh tokens without the refresh claim should throw a BadCredentialsException`() {
        val token = JWT.create().withSubject("test-user").sign(algorithm)
        assertThrows<BadCredentialsException> { verifier.verifyRefreshToken(token) }
    }
}
