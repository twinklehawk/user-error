package net.plshark.users.auth.jwt

import org.junit.jupiter.api.Test
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import reactor.test.StepVerifier

class ServerHttpJwtAuthenticationConverterTest {

    private val converter = ServerHttpJwtAuthenticationConverter()

    @Test
    fun `should parse out the token from the authorization header`() {
        val exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("http://test/url").header("Authorization", "Bearer test-token"))

        StepVerifier.create(converter.convert(exchange))
            .expectNext(JwtAuthenticationToken(
                username = null,
                token = "test-token",
                authenticated = false,
                authorities = setOf()
            )).verifyComplete()
    }

    @Test
    fun `should return empty when there is no authorization header`() {
        val exchange = MockServerWebExchange.from(MockServerHttpRequest.get("http://test/url"))

        StepVerifier.create(converter.convert(exchange))
            .verifyComplete()
    }

    @Test
    fun `should return empty when the header value does not start with Bearer`() {
        val exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("http://test/url").header("Authorization", "test-token"))

        StepVerifier.create(converter.convert(exchange))
            .verifyComplete()
    }
}
