package net.plshark.users.auth.jwt

import net.plshark.users.auth.model.AuthenticatedUser
import net.plshark.users.auth.service.AuthService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class JwtReactiveAuthenticationManagerSpec extends Specification {

    AuthService authService = Mock()
    JwtReactiveAuthenticationManager manager = new JwtReactiveAuthenticationManager(authService)

    def 'should parse the username and authorities from the token and set the authorized flag'() {
        def token = JwtAuthenticationToken.builder().token('test-token').build()
        authService.validateToken('test-token') >> Mono.just(AuthenticatedUser.builder()
                .username('test-user')
                .authority('a')
                .authority('b')
                .build())

        expect:
        StepVerifier.create(manager.authenticate(token))
                .expectNext(JwtAuthenticationToken.builder().username('test-user').authenticated(true)
                        .authorityName('a').authorityName('b').build())
                .verifyComplete()
    }

    def 'an invalid token should throw a BadCredentialsException'() {
        def token = JwtAuthenticationToken.builder().token('bad-token').build()
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
