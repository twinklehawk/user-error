package net.plshark.users.auth.webservice

import net.plshark.users.auth.model.AccountCredentials
import net.plshark.users.auth.model.AuthToken
import net.plshark.users.auth.model.AuthenticatedUser
import net.plshark.users.auth.service.AuthService
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class AuthControllerSpec extends Specification {

    def service = Mock(AuthService)
    def controller = new AuthController(service)

    def 'authenticate should pass the credentials through to the service'() {
        def token = AuthToken.builder().accessToken('access').tokenType('type').expiresIn(1)
                .refreshToken('refresh').scope('scope').build()
        service.authenticate(AccountCredentials.create('test-user', 'test-password')) >> Mono.just(token)

        expect:
        StepVerifier.create(controller.authenticate(AccountCredentials.create('test-user', 'test-password')))
                .expectNext(token)
                .verifyComplete()
    }

    def 'refresh should pass the token through to the service'() {
        def token = AuthToken.builder().accessToken('access').tokenType('type').expiresIn(1)
                .refreshToken('refresh').scope('scope').build()
        service.refresh('test-token') >> Mono.just(token)

        expect:
        StepVerifier.create(controller.refresh('test-token'))
                .expectNext(token)
                .verifyComplete()
    }

    def 'validateToken should pass the token through to the service'() {
        service.validateToken('refresh') >> Mono.just(AuthenticatedUser.create('user', Collections.emptySet()))

        expect:
        StepVerifier.create(controller.validateToken('refresh'))
                .expectNext(AuthenticatedUser.create('user', Collections.emptySet()))
                .verifyComplete()
    }
}
