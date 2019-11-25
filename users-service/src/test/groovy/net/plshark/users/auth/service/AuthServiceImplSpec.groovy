package net.plshark.users.auth.service

import net.plshark.users.auth.model.AccountCredentials
import net.plshark.users.auth.model.AuthToken
import net.plshark.users.auth.model.AuthenticatedUser
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class AuthServiceImplSpec extends Specification {

    def passwordEncoder = Mock(PasswordEncoder)
    def userDetailsService = Mock(ReactiveUserDetailsService)
    def tokenVerifier = Mock(TokenVerifier)
    def tokenBuilder = Mock(TokenBuilder)
    def service = new AuthServiceImpl(passwordEncoder, userDetailsService, tokenVerifier, tokenBuilder, 1000L)

    def 'authenticate should build access and refresh tokens with the correct expiration'() {
        userDetailsService.findByUsername('test-user') >> Mono.just(new User('test-user', 'encoded-password', Collections.emptyList()))
        passwordEncoder.matches('test-password', 'encoded-password') >> true
        tokenBuilder.buildAccessToken('test-user', 1000L, [] as String[]) >> 'test-token'
        tokenBuilder.buildRefreshToken('test-user', 1000L) >> 'refresh-token'

        expect:
        StepVerifier.create(service.authenticate(AccountCredentials.create('test-user', 'test-password')))
                .expectNext(AuthToken.builder().accessToken('test-token').expiresIn(1L).refreshToken('refresh-token').build())
                .verifyComplete()
    }

    def 'authenticate should return an exception if no matching user is found'() {
        userDetailsService.findByUsername('test-user') >> Mono.empty()

        expect:
        StepVerifier.create(service.authenticate(AccountCredentials.create('test-user', 'test-password')))
                .verifyError(BadCredentialsException)
    }

    def 'authenticate should return an exception if the credentials are invalid'() {
        userDetailsService.findByUsername('test-user') >> Mono.just(new User('test-user', 'encoded-password', Collections.emptyList()))
        passwordEncoder.matches('test-password', 'encoded-password') >> false

        expect:
        StepVerifier.create(service.authenticate(AccountCredentials.create('test-user', 'test-password')))
                .verifyError(BadCredentialsException)
    }

    def 'refresh should build new access and refresh tokens with the correct expiration'() {
        tokenVerifier.verifyRefreshToken('refresh-token') >> 'test-user'
        userDetailsService.findByUsername('test-user') >> Mono.just(new User('test-user', 'encoded-password', Collections.emptyList()))
        tokenBuilder.buildAccessToken('test-user', 1000L, [] as String[]) >> 'test-token'
        tokenBuilder.buildRefreshToken('test-user', 1000L) >> 'refresh-token'

        expect:
        StepVerifier.create(service.refresh('refresh-token'))
                .expectNext(AuthToken.builder().accessToken('test-token').expiresIn(1L).refreshToken('refresh-token').build())
                .verifyComplete()
    }

    def 'refresh should return an exception if the token is invalid'() {
        tokenVerifier.verifyRefreshToken('refresh-token') >> { throw new BadCredentialsException('test exception') }

        expect:
        StepVerifier.create(service.refresh('refresh-token'))
                .verifyError(BadCredentialsException)
    }

    def 'refresh should return an exception if the corresponding user is not found'() {
        tokenVerifier.verifyRefreshToken('refresh-token') >> 'test-user'
        userDetailsService.findByUsername('test-user') >> Mono.empty()

        expect:
        StepVerifier.create(service.refresh('refresh-token'))
                .verifyError(BadCredentialsException)
    }

    def 'validate should complete successfully for a valid token'() {
        def user = AuthenticatedUser.builder()
                .username('test-user')
                .authorities(Collections.emptySet())
                .build()
        tokenVerifier.verifyToken('access-token') >> user

        expect:
        StepVerifier.create(service.validateToken('access-token'))
                .expectNext(user)
                .verifyComplete()
    }

    def 'validate should return an exception for an invalid token'() {
        tokenVerifier.verifyToken('access-token') >> { throw new BadCredentialsException('test exception') }

        expect:
        StepVerifier.create(service.validateToken('access-token'))
                .verifyError(BadCredentialsException)
    }
}
