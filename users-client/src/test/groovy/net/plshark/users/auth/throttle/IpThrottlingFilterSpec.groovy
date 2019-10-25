package net.plshark.users.auth.throttle

import java.util.concurrent.TimeUnit
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class IpThrottlingFilterSpec extends Specification {

    IpThrottlingFilter filter = new IpThrottlingFilter(2, 5, TimeUnit.SECONDS)
    ServerHttpRequest request = Mock()
    HttpHeaders headers = Mock()
    ServerHttpResponse response = Mock()
    ServerWebExchange exchange = Mock()
    WebFilterChain chain = Mock()
    PublisherProbe probe = PublisherProbe.empty()

    def setup() {
        exchange.getRequest() >> request
        exchange.getResponse() >> response
        request.getHeaders() >> headers
        chain.filter(exchange) >> probe.mono()
    }

    def "should pull the correct IP and username when the forwarded header is not set and continue execution if they are not blocked"() {
        headers.getFirst("X-Forwarded-For") >> null
        request.getRemoteAddress() >> InetSocketAddress.createUnresolved("192.168.1.2", 80)

        expect:
        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "should pull the correct IP and username when the forwarded header is set and continue execution if they are not blocked"() {
        headers.getFirst("X-Forwarded-For") >> "192.168.1.2"
        request.getRemoteAddress() >> null

        expect:
        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "should block the request if the IP has made too many requests"() {
        request.getRemoteAddress() >> InetSocketAddress.createUnresolved("192.168.1.2", 80)

        when:
        for (int i = 0; i < 3; ++i)
            StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()

        then:
        1 * response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
    }
}
