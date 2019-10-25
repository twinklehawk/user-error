package net.plshark.users.auth.throttle

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class LoginAttemptThrottlingFilterSpec extends Specification {

    LoginAttemptService service = Mock()
    UsernameExtractor extractor = Mock()
    LoginAttemptThrottlingFilter filter = new LoginAttemptThrottlingFilter(service, extractor)
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
        extractor.extractUsername(request) >> Optional.of("test-user")
        service.isIpBlocked("192.168.1.2") >> false
        service.isUsernameBlocked("test-user") >> false

        expect:
        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "should pull the correct IP and username when the forwarded header is set and continue execution if they are not blocked"() {
        headers.getFirst("X-Forwarded-For") >> "192.168.1.2"
        request.getRemoteAddress() >> null
        extractor.extractUsername(request) >> Optional.of("test-user")
        service.isIpBlocked("192.168.1.2") >> false
        service.isUsernameBlocked("test-user") >> false

        expect:
        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "should block the request if the username is blocked"() {
        request.getRemoteAddress() >> InetSocketAddress.createUnresolved("192.168.1.2", 80)
        extractor.extractUsername(request) >> Optional.of("test-user")
        service.isIpBlocked("192.168.1.2") >> false
        service.isUsernameBlocked("test-user") >> true

        when:
        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()

        then:
        probe.assertWasNotSubscribed()
        1 * response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
    }

    def "should block the request if the IP is blocked"() {
        request.getRemoteAddress() >> InetSocketAddress.createUnresolved("192.168.1.2", 80)
        extractor.extractUsername(request) >> Optional.of("test-user")
        service.isIpBlocked("192.168.1.2") >> true
        service.isUsernameBlocked("test-user") >> false

        when:
        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()

        then:
        probe.assertWasNotSubscribed()
        1 * response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
    }

    def "should use a blank IP address if the host cannot be found"() {
        request.getRemoteAddress() >> null
        extractor.extractUsername(request) >> Optional.of("test-user")
        service.isUsernameBlocked("test-user") >> false
        service.isIpBlocked("") >> true

        when:
        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()

        then:
        probe.assertWasNotSubscribed()
        1 * response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
    }

    def "should use a blank username if the request does not include a username"() {
        request.getRemoteAddress() >> InetSocketAddress.createUnresolved("192.168.1.2", 80)
        extractor.extractUsername(request) >> Optional.empty()
        service.isUsernameBlocked("") >> true
        service.isIpBlocked("192.168.1.2") >> false

        when:
        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete()

        then:
        probe.assertWasNotSubscribed()
        1 * response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
    }
}
