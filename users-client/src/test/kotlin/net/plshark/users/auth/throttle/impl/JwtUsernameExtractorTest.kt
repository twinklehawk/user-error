package net.plshark.users.auth.throttle.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest

class JwtUsernameExtractorTest {

    private val algorithm = Algorithm.HMAC256("test")
    private val verifier = JWT.require(algorithm).build()
    private val extractor = JwtUsernameExtractor(verifier)
    private val request = mockk<ServerHttpRequest>()
    private val headers = mockk<HttpHeaders>()

    @BeforeEach
    fun setup() {
        every { request.headers } returns headers
    }

    @Test
    fun `should extract the username if it is present`() {
        val token = JWT.create().withSubject("test-user").sign(algorithm)
        every { headers.getFirst("Authorization") } returns "Bearer $token"

        assertEquals("test-user", extractor.extractUsername(request).get())
    }

    @Test
    fun `should return an empty optional if the username is not present in the token`() {
        val token = JWT.create().sign(algorithm)
        every { headers.getFirst("Authorization") } returns "Bearer $token"

        assertFalse(extractor.extractUsername(request).isPresent)
    }

    @Test
    fun `should return an empty optional if the header value does not start with Bearer`() {
        val token = JWT.create().withSubject("test-user").sign(algorithm)
        every { headers.getFirst("Authorization") } returns token

        assertFalse(extractor.extractUsername(request).isPresent)
    }

    @Test
    fun `should return an empty optional if the JWT is invalid`() {
        every { headers.getFirst("Authorization") } returns "Bearer abc123"

        assertFalse(extractor.extractUsername(request).isPresent)
    }

    @Test
    fun `should return an empty optional if the auth header is not present`() {
        every { headers.getFirst("Authorization") } returns null

        assertFalse(extractor.extractUsername(request).isPresent)
    }
}
