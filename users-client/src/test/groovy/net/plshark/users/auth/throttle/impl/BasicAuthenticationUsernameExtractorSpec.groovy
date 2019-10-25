package net.plshark.users.auth.throttle.impl

import java.nio.charset.StandardCharsets
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import spock.lang.Specification

class BasicAuthenticationUsernameExtractorSpec extends Specification {

    ServerHttpRequest request = Mock()
    HttpHeaders headers = Mock()

    def setup() {
        request.getHeaders() >> headers
    }

    def 'should extract the username if it is present'() {
        headers.getFirst('Authorization') >> 'Basic ' +
                new String(Base64.getEncoder().encode('username:password'.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)

        expect:
        new BasicAuthenticationUsernameExtractor().extractUsername(request).get() == 'username'
    }

    def 'should return an empty optional if the username is not present in the header'() {
        headers.getFirst('Authorization') >> 'Basic ' +
                new String(Base64.getEncoder().encode('password'.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)

        expect:
        !new BasicAuthenticationUsernameExtractor().extractUsername(request).isPresent()
    }

    def 'should return an empty optional if the header value does not start with Basic'() {
        headers.getFirst('Authorization') >> 'Something else'

        expect:
        !new BasicAuthenticationUsernameExtractor().extractUsername(request).isPresent()
    }

    def 'should return an empty optional if the basic auth header is not present'() {
        headers.getFirst('Authorization') >> null

        expect:
        !new BasicAuthenticationUsernameExtractor().extractUsername(request).isPresent()
    }

    def 'invalid base64 encoding in auth header value returns an empty optional'() {
        headers.getFirst("Authorization") >> "Basic 1234"

        expect:
        !new BasicAuthenticationUsernameExtractor().extractUsername(request).isPresent()
    }
}
