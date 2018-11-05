package net.plshark.auth.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import reactor.test.StepVerifier
import spock.lang.Specification

class JwtReactiveAuthenticationManagerSpec extends Specification {

    Algorithm algorithm = Algorithm.HMAC256('test-secret')
    JWTVerifier verifier = JWT.require(algorithm).build()
    JwtReactiveAuthenticationManager manager = new JwtReactiveAuthenticationManager(verifier)

    def 'should parse the username and authorities from the token and set the authorized flag'() {
        def str = JWT.create().withSubject('test-user').withArrayClaim(JwtReactiveAuthenticationManager.AUTHORITIES_CLAIM,
                ['a', 'b'] as String[]).sign(algorithm)
        def token = JwtAuthenticationToken.builder().withToken(str).build()

        expect:
        StepVerifier.create(manager.authenticate(token))
                .expectNext(JwtAuthenticationToken.builder().withUsername('test-user').withAuthenticated(true)
                        .withAuthority('a').withAuthority('b').build())
                .verifyComplete()
    }

    def 'an invalid token should throw a BadCredentialsException'() {
        def token = JwtAuthenticationToken.builder().withToken('bad-token').build()

        expect:
        StepVerifier.create(manager.authenticate(token))
                .verifyError(BadCredentialsException.class)
    }

    def 'an empty authorities claim should create a token with an empty authorities list'() {
        def str = JWT.create().withSubject('test-user').sign(algorithm)
        def token = JwtAuthenticationToken.builder().withToken(str).build()

        expect:
        StepVerifier.create(manager.authenticate(token))
                .expectNext(JwtAuthenticationToken.builder().withUsername('test-user').withAuthenticated(true).build())
                .verifyComplete()
    }

    def 'a non-jwt authorization should throw a BadCredentialsException'() {
        def token = new UsernamePasswordAuthenticationToken('user', 'pass')

        expect:
        StepVerifier.create(manager.authenticate(token))
                .verifyError(BadCredentialsException.class)
    }
}
