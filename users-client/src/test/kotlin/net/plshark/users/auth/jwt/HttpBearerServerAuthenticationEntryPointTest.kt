package net.plshark.users.auth.jwt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.authentication.BadCredentialsException
import reactor.test.StepVerifier

class HttpBearerServerAuthenticationEntryPointTest {

    private val exchange = MockServerWebExchange.from(MockServerHttpRequest.get("http://test/url"))
    private val entryPoint = HttpBearerServerAuthenticationEntryPoint("test-realm")

    @Test
    fun `should set the status code and the WWW-Authenticate header`() {
        StepVerifier.create(entryPoint.commence(exchange, BadCredentialsException("test")))
            .verifyComplete()

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.response.statusCode)
        assertEquals("""Bearer realm="test-realm"""", exchange.response.headers.getFirst("WWW-Authenticate"))
    }
}
