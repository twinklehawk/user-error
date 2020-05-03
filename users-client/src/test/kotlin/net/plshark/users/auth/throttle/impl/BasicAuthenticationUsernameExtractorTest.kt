package net.plshark.users.auth.throttle.impl

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import java.util.*

class BasicAuthenticationUsernameExtractorTest {

    private val request = mockk<ServerHttpRequest>()
    private val headers = mockk<HttpHeaders>()
    private val extractor = BasicAuthenticationUsernameExtractor()

    @BeforeEach
    fun setup() {
        every { request.headers } returns headers
    }

    @Test
    fun `should extract the username if it is present`() {
        every { headers.getFirst("Authorization") } returns "Basic " +
                String(Base64.getEncoder().encode("username:password".toByteArray()), StandardCharsets.UTF_8)

        assertEquals("username", extractor.extractUsername(request).get())
    }

    @Test
    fun `should return an empty optional if the username is not present in the header`() {
        every { headers.getFirst("Authorization") } returns "Basic " +
                String(Base64.getEncoder().encode("password".toByteArray()), StandardCharsets.UTF_8)
        
        assertFalse(extractor.extractUsername(request).isPresent)
    }

    @Test
    fun `should return an empty optional if the header value does not start with Basic`() {
        every { headers.getFirst("Authorization") } returns "Something else"

        assertFalse(extractor.extractUsername(request).isPresent)
    }

    @Test
    fun `should return an empty optional if the basic auth header is not present`() {
        every { headers.getFirst("Authorization") } returns null

        assertFalse(extractor.extractUsername(request).isPresent)
    }

    @Test
    fun `invalid base64 encoding in auth header value returns an empty optional`() {
        every { headers.getFirst("Authorization") } returns "Basic 1234"

        assertFalse(extractor.extractUsername(request).isPresent)
    }
}
