package net.plshark.users.service

import net.plshark.errors.DuplicateException


import org.springframework.dao.DataIntegrityViolationException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class ApplicationsServiceImplSpec extends Specification {

    def appsRepo = Mock(ApplicationsRepository)
    def rolesRepo = Mock(RolesRepository)
    def service = new ApplicationsServiceImpl(appsRepo, rolesRepo)

    def 'get should pass through the response from the repo'() {
        def app = Application.builder().id(1).name('app').build()
        appsRepo.get('app') >> Mono.just(app)

        expect:
        StepVerifier.create(service.get('app'))
                .expectNext(app)
                .verifyComplete()
    }

    def 'create should pass through the response from the repo'() {
        def request = Application.builder().name('app').build()
        def inserted = request.toBuilder().id(1).build()
        appsRepo.insert(request) >> Mono.just(inserted)

        expect:
        StepVerifier.create(service.create(request))
                .expectNext(inserted)
                .verifyComplete()
    }

    def 'create should map the exception for a duplicate name to a DuplicateException'() {
        def request = Application.builder().name('app').build()
        appsRepo.insert(request) >> Mono.error(new DataIntegrityViolationException("test error"))

        expect:
        StepVerifier.create(service.create(request))
                .verifyError(DuplicateException)
    }

    def 'delete should delete the app'() {
        def app = Application.builder().id(1).name('app').build()
        appsRepo.get('app') >> Mono.just(app)
        PublisherProbe deleteAppProbe = PublisherProbe.empty()
        appsRepo.delete(1) >> deleteAppProbe.mono()

        expect:
        StepVerifier.create(service.delete('app'))
                .verifyComplete()
        deleteAppProbe.assertWasSubscribed()
    }

    def 'getApplicationRoles should pass through the response from the repo'() {
        def role1 = Role.builder().id(1).name('role1').applicationId(100).build()
        def role2 = Role.builder().id(2).name('role2').applicationId(100).build()
        rolesRepo.getRolesForApplication(100) >> Flux.just(role1, role2)

        expect:
        StepVerifier.create(service.getApplicationRoles(100))
                .expectNext(role1, role2)
                .verifyComplete()
    }
}
