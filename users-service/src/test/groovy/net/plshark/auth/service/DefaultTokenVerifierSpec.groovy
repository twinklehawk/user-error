package net.plshark.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.security.authentication.BadCredentialsException
import reactor.test.StepVerifier
import spock.lang.Specification

class DefaultTokenVerifierSpec extends Specification {

    def algorithm = Algorithm.HMAC256('test-key')
    def jwtVerifier = JWT.require(algorithm).build()
    def verifier = new DefaultTokenVerifier(jwtVerifier)

    def 'valid access tokens should return the username'() {
        when:
        def token = JWT.create().withSubject('test-user').sign(algorithm)

        then:
        StepVerifier.create(verifier.verifyToken(token))
                .expectNext('test-user')
                .verifyComplete()
    }

    def 'invalid access tokens should throw a BadCredentialsException'() {
        expect:
        StepVerifier.create(verifier.verifyToken('bad-token'))
                .verifyError(BadCredentialsException)
    }

    def 'valid refresh tokens should return the username'() {
        when:
        def token = JWT.create().withSubject('test-user').withClaim(AuthService.REFRESH_CLAIM, true).sign(algorithm)

        then:
        StepVerifier.create(verifier.verifyRefreshToken(token))
                .expectNext('test-user')
                .verifyComplete()
    }

    def 'invalid refresh tokens should throw a BadCredentialsException'() {
        expect:
        StepVerifier.create(verifier.verifyRefreshToken('bad-token'))
                .verifyError(BadCredentialsException)
    }

    def 'refresh tokens without the refresh claim should throw a BadCredentialsException'() {
        when:
        def token = JWT.create().withSubject('test-user').sign(algorithm)

        then:
        StepVerifier.create(verifier.verifyRefreshToken(token))
                .verifyError(BadCredentialsException)
    }
}
