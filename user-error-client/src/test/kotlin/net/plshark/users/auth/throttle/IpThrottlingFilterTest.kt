package net.plshark.users.auth.throttle

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import java.net.InetSocketAddress

class IpThrottlingFilterTest {

    private val filter = IpThrottlingFilter(2, 5, TimeUnit.SECONDS)
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

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `should pull the correct IP and username when the forwarded header is set and continue execution if they are not blocked`() {
        every { headers.getFirst("X-Forwarded-For") } returns "192.168.1.2"
        every { request.remoteAddress } returns null

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `should block the request if the IP has made too many requests`() {
        every { request.remoteAddress } returns InetSocketAddress.createUnresolved("192.168.1.2", 80)
        every { headers.getFirst("X-Forwarded-For") } returns null
        every { response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS) } returns true

        for (x in 0 until 3)
            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()

        verify { response.statusCode = HttpStatus.TOO_MANY_REQUESTS }
    }
}
