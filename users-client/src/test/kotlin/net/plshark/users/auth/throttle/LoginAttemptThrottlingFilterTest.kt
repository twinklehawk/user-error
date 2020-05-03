package net.plshark.users.auth.throttle

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import java.net.InetSocketAddress
import java.util.*

class LoginAttemptThrottlingFilterTest {

    private val service = mockk<LoginAttemptService>()
    private val extractor = mockk<UsernameExtractor>()
    private val filter = LoginAttemptThrottlingFilter(service, extractor)
    private val request = mockk<ServerHttpRequest>()
    private val headers = mockk<HttpHeaders>()
    private val response = mockk<ServerHttpResponse>()
    private val exchange = mockk<ServerWebExchange>()
    private val chain = mockk<WebFilterChain>()
    private val probe = PublisherProbe.empty<Void>()

    @BeforeEach
    fun setup() {
        every { exchange.request } returns request
        every { exchange.response } returns response
        every { request.headers } returns headers
        every { chain.filter(exchange) } returns probe.mono()
    }

    @Test
    fun `should pull the correct IP and username when the forwarded header is not set and continue execution if they are not blocked`() {
        every { headers.getFirst("X-Forwarded-For") } returns null
        every { request.remoteAddress } returns InetSocketAddress.createUnresolved("192.168.1.2", 80)
        every { extractor.extractUsername(request) } returns Optional.of("test-user")
        every { service.isIpBlocked("192.168.1.2") } returns false
        every { service.isUsernameBlocked("test-user") } returns false
        every { response.statusCode } returns HttpStatus.OK

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `should pull the correct IP and username when the forwarded header is set and continue execution if they are not blocked`() {
        every { headers.getFirst("X-Forwarded-For") } returns "192.168.1.2"
        every { request.remoteAddress } returns null
        every { extractor.extractUsername(request) } returns Optional.of("test-user")
        every { service.isIpBlocked("192.168.1.2") } returns false
        every { service.isUsernameBlocked("test-user") } returns false
        every { response.statusCode } returns HttpStatus.OK

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `should block the request if the username is blocked`() {
        every { request.remoteAddress } returns InetSocketAddress.createUnresolved("192.168.1.2", 80)
        every { extractor.extractUsername(request) } returns Optional.of("test-user")
        every { service.isIpBlocked("192.168.1.2") } returns false
        every { service.isUsernameBlocked("test-user") } returns true
        every { headers.getFirst("X-Forwarded-For") } returns null
        every { response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS) } returns true

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()
        probe.assertWasNotSubscribed()
        verify { response.statusCode = HttpStatus.TOO_MANY_REQUESTS }
    }

    @Test
    fun `should block the request if the IP is blocked`() {
        every { request.remoteAddress } returns InetSocketAddress.createUnresolved("192.168.1.2", 80)
        every { extractor.extractUsername(request) } returns Optional.of("test-user")
        every { service.isIpBlocked("192.168.1.2") } returns true
        every { service.isUsernameBlocked("test-user") } returns false
        every { headers.getFirst("X-Forwarded-For") } returns null
        every { response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS) } returns true

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()
        probe.assertWasNotSubscribed()
        verify { response.statusCode = HttpStatus.TOO_MANY_REQUESTS }
    }

    @Test
    fun `should use a blank IP address if the host cannot be found`() {
        every { request.remoteAddress } returns null
        every { extractor.extractUsername(request) } returns Optional.of("test-user")
        every { service.isUsernameBlocked("test-user") } returns false
        every { service.isIpBlocked("") } returns true
        every { headers.getFirst("X-Forwarded-For") } returns null
        every { response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS) } returns true

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()
        probe.assertWasNotSubscribed()
        verify { response.statusCode = HttpStatus.TOO_MANY_REQUESTS }
    }

    @Test
    fun `should use a blank username if the request does not include a username`() {
        every { request.remoteAddress } returns InetSocketAddress.createUnresolved("192.168.1.2", 80)
        every { extractor.extractUsername(request) } returns Optional.empty()
        every { service.isUsernameBlocked("") } returns true
        every { service.isIpBlocked("192.168.1.2") } returns false
        every { headers.getFirst("X-Forwarded-For") } returns null
        every { response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS) } returns true

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()
        probe.assertWasNotSubscribed()
        verify { response.statusCode = HttpStatus.TOO_MANY_REQUESTS }
    }
}
