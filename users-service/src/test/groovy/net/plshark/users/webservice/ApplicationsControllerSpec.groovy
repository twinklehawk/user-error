package net.plshark.users.webservice

import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Application
import net.plshark.users.service.ApplicationsService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class ApplicationsControllerSpec extends Specification {

    def applicationsService = Mock(ApplicationsService)
    def controller = new ApplicationsController(applicationsService)

    def 'getting an application should pass through whatever the service returns'() {
        def app = Application.builder().id(123L).name('test-app').build()
        applicationsService.get('test-app') >> Mono.just(app)

        expect:
        StepVerifier.create(controller.get('test-app'))
                .expectNext(app)
                .verifyComplete()
    }

    def 'getting an application should throw an exception when the application does not exist'() {
        applicationsService.get('test-app') >> Mono.empty()

        expect:
        StepVerifier.create(controller.get('test-app'))
                .verifyError(ObjectNotFoundException)
    }

    def 'getting all applications should pass through whatever the service returns'() {
        def app1 = Application.builder().id(1L).name('app1').build()
        def app2 = Application.builder().id(2L).name('app2').build()
        applicationsService.getApplications(100, 0) >> Flux.just(app1, app2)

        expect:
        StepVerifier.create(controller.getApplications(100, 0))
                .expectNext(app1)
                .expectNext(app2)
                .verifyComplete()
    }

    def 'inserting should pass through whatever the service returns'() {
        def request = Application.builder().name('name').build()
        def created = Application.builder().id(1L).name('name').build()
        applicationsService.create(request) >> Mono.just(created)

        expect:
        StepVerifier.create(controller.create(request))
                .expectNext(created)
                .verifyComplete()
    }

    def 'deleting should complete when the service completes'() {
        applicationsService.delete('test-app') >> Mono.empty()

        expect:
        StepVerifier.create(controller.delete('test-app'))
                .verifyComplete()
    }
}
