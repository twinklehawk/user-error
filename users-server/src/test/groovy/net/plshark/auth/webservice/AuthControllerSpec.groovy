package net.plshark.auth.webservice

import net.plshark.auth.model.AccountCredentials
import net.plshark.auth.model.AuthToken
import net.plshark.auth.service.AuthService
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class AuthControllerSpec extends Specification {

    def service = Mock(AuthService)
    def controller = new AuthController(service)

    def 'authenticate should pass the credentials through to the service'() {
        def token = new AuthToken('access', 'type', 1, 'refresh', 'scope')
        service.authenticate(new AccountCredentials('test-user', 'test-password')) >>
                Mono.just(token)

        expect:
        StepVerifier.create(controller.authenticate(new AccountCredentials('test-user', 'test-password')))
                .expectNext(token)
                .verifyComplete()
    }

    def 'refresh should pass the token through to the service'() {
        def token = new AuthToken('access', 'type', 1, 'refresh', 'scope')
        service.refresh('test-token') >>
                Mono.just(token)

        expect:
        StepVerifier.create(controller.refresh('test-token'))
                .expectNext(token)
                .verifyComplete()
    }
}
