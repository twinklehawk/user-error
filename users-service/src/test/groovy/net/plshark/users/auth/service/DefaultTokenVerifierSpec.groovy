package net.plshark.users.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

import org.springframework.security.authentication.BadCredentialsException
import spock.lang.Specification

class DefaultTokenVerifierSpec extends Specification {

    def algorithm = Algorithm.HMAC256('test-key')
    def jwtVerifier = JWT.require(algorithm).build()
    def verifier = new DefaultTokenVerifier(jwtVerifier)

    def 'valid access tokens should return the username and authorities'() {
        when:
        def token = JWT.create().withSubject('test-user')
                .withArrayClaim(PlsharkClaim.AUTHORITIES, ['user'] as String[]).sign(algorithm)

        then:
        verifier.verifyToken(token) == AuthenticatedUser.builder()
                .username('test-user')
                .authority('user')
                .build()
    }

    def 'no authorities claim should build an empty authorities list'() {
        when:
        def token = JWT.create().withSubject('test-user').sign(algorithm)

        then:
        verifier.verifyToken(token) == AuthenticatedUser.builder()
                .username('test-user')
                .authorities(Collections.emptySet())
                .build()
    }

    def 'invalid access tokens should throw a BadCredentialsException'() {
        when:
        verifier.verifyToken('bad-token')

        then:
        thrown(BadCredentialsException)
    }

    def 'valid refresh tokens should return the username'() {
        when:
        def token = JWT.create().withSubject('test-user').withClaim(PlsharkClaim.REFRESH, true).sign(algorithm)

        then:
        verifier.verifyRefreshToken(token) == 'test-user'
    }

    def 'invalid refresh tokens should throw a BadCredentialsException'() {
        when:
        verifier.verifyRefreshToken('bad-token')

        then:
        thrown(BadCredentialsException)
    }

    def 'refresh tokens without the refresh claim should throw a BadCredentialsException'() {
        when:
        def token = JWT.create().withSubject('test-user').sign(algorithm)
        verifier.verifyRefreshToken(token)

        then:
        thrown(BadCredentialsException)
    }
}
