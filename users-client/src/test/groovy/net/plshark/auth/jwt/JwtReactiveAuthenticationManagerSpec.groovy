package net.plshark.auth.jwt

import net.plshark.auth.model.AuthenticatedUser
import net.plshark.auth.service.AuthService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class JwtReactiveAuthenticationManagerSpec extends Specification {

    AuthService authService = Mock()
    JwtReactiveAuthenticationManager manager = new JwtReactiveAuthenticationManager(authService)

    def 'should parse the username and authorities from the token and set the authorized flag'() {
        def token = JwtAuthenticationToken.builder().withToken('test-token').build()
        authService.validateToken('test-token') >> Mono.just(new AuthenticatedUser('test-user', 'a', 'b'))

        expect:
        StepVerifier.create(manager.authenticate(token))
                .expectNext(JwtAuthenticationToken.builder().withUsername('test-user').withAuthenticated(true)
                        .withAuthority('a').withAuthority('b').build())
                .verifyComplete()
    }

    def 'an invalid token should throw a BadCredentialsException'() {
        def token = JwtAuthenticationToken.builder().withToken('bad-token').build()
        authService.validateToken('bad-token') >> Mono.error({ new BadCredentialsException('bad') })

        expect:
        StepVerifier.create(manager.authenticate(token))
                .verifyError(BadCredentialsException.class)
    }

    def 'a non-jwt authorization should throw a BadCredentialsException'() {
        def token = new UsernamePasswordAuthenticationToken('user', 'pass')

        expect:
        StepVerifier.create(manager.authenticate(token))
                .verifyError(BadCredentialsException.class)
    }
}
