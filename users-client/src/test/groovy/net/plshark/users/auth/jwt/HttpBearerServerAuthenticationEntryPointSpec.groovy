package net.plshark.users.auth.jwt

import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.authentication.BadCredentialsException
import reactor.test.StepVerifier
import spock.lang.Specification

class HttpBearerServerAuthenticationEntryPointSpec extends Specification {

    def exchange = MockServerWebExchange.from(MockServerHttpRequest.get('http://test/url'))
    def entryPoint = new HttpBearerServerAuthenticationEntryPoint('test-realm')

    def 'should set the status code and the WWW-Authenticate header'() {
        when:
        StepVerifier.create(entryPoint.commence(exchange, new BadCredentialsException('test')))
                .verifyComplete()

        then:
        exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED
        exchange.getResponse().getHeaders().getFirst('WWW-Authenticate') == 'Bearer realm="test-realm"'
    }
}
