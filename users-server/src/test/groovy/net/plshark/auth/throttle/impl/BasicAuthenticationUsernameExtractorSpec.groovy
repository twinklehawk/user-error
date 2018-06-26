package net.plshark.auth.throttle.impl

import java.nio.charset.StandardCharsets
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest

import spock.lang.Specification

class BasicAuthenticationUsernameExtractorSpec extends Specification {

    ServerHttpRequest request = Mock()
    HttpHeaders headers = Mock()
    BasicAuthenticationUsernameExtractor extractor = new BasicAuthenticationUsernameExtractor()

    def setup() {
        request.getHeaders() >> headers
    }

    def "valid basic auth returns the username"() {
        headers.getFirst("Authorization") >> "Basic dGVzdC11c2VyOnBhc3N3b3Jk"

        expect:
        extractor.extractUsername(request).get() == "test-user"
    }

    def "no Authorization header returns an empty optional"() {
        headers.getFirst("Authorization") >> null

        expect:
        !extractor.extractUsername(request).isPresent()

    }

    def "Authorization header not starting with Basic returns an empty optional"() {
        headers.getFirst("Authorization") >> "dGVzdC11c2VyOnBhc3N3b3Jk"

        expect:
        !extractor.extractUsername(request).isPresent()

    }

    def "invalid base64 encoding in auth header value returns an empty optional"() {
        headers.getFirst("Authorization") >> "Basic 1234"

        expect:
        !extractor.extractUsername(request).isPresent()

    }

    def "no colon in auth header value returns an empty optional"() {
        String result = new String(Base64.getEncoder().encode("test-user".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)
        headers.getFirst("Authorization") >> result

        expect:
        !extractor.extractUsername(request).isPresent()
    }
}
