package net.plshark.users.auth.throttle.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import spock.lang.Specification

class JwtUsernameExtractorSpec extends Specification {

    Algorithm algorithm = Algorithm.HMAC256("test")
    JWTVerifier verifier = JWT.require(algorithm).build()
    JwtUsernameExtractor extractor = new JwtUsernameExtractor(verifier)
    ServerHttpRequest request = Mock()
    HttpHeaders headers = Mock()

    def setup() {
        request.getHeaders() >> headers
    }

    def 'should extract the username if it is present'() {
        def token = JWT.create().withSubject('test-user').sign(algorithm)
        headers.getFirst('Authorization') >> 'Bearer ' + token

        expect:
        extractor.extractUsername(request).get() == 'test-user'
    }

    def 'should return an empty optional if the username is not present in the token'() {
        def token = JWT.create().sign(algorithm)
        headers.getFirst('Authorization') >> 'Bearer ' + token

        expect:
        !extractor.extractUsername(request).isPresent()
    }

    def 'should return an empty optional if the header value does not start with Bearer'() {
        def token = JWT.create().withSubject('test-user').sign(algorithm)
        headers.getFirst('Authorization') >> token

        expect:
        !extractor.extractUsername(request).isPresent()
    }

    def 'should return an empty optional if the JWT is invalid'() {
        headers.getFirst('Authorization') >> 'Bearer abc123'

        expect:
        !extractor.extractUsername(request).isPresent()
    }

    def 'should return an empty optional if the auth header is not present'() {
        headers.getFirst('Authorization') >> null

        expect:
        !extractor.extractUsername(request).isPresent()
    }
}
