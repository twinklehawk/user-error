package net.plshark.users.service

import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.repo.ApplicationsRepository
import net.plshark.users.repo.RolesRepository
import org.springframework.dao.DataIntegrityViolationException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class RolesServiceImplSpec extends Specification {

    RolesRepository rolesRepo = Mock()
    ApplicationsRepository appsRepo = Mock()
    def service = new RolesServiceImpl(rolesRepo, appsRepo)

    def 'creating should save a role and return the saved role'() {
        def role = Role.builder().name('role').applicationId(123).build()
        def inserted = role.toBuilder().id(1).build()
        rolesRepo.insert(role) >> Mono.just(inserted)

        expect:
        StepVerifier.create(service.create(role))
                .expectNext(inserted)
                .verifyComplete()
    }

    def 'creating with an application name should look up the application and set the ID before inserting'() {
        def inserted = Role.builder().id(1).name('role').applicationId(321).build()
        appsRepo.get('app') >> Mono.just(Application.builder().id(321).name('app').build())
        rolesRepo.insert(Role.builder().name('role').applicationId(321).build()) >> Mono.just(inserted)

        expect:
        StepVerifier.create(service.create('app', Role.builder().name('role').build()))
                .expectNext(inserted)
                .verifyComplete()
    }

    def 'create should map the exception for a duplicate name to a DuplicateException'() {
        def request = Role.builder().name('app').build()
        appsRepo.get('app') >> Mono.just(Application.builder().id(321).name('app').build())
        rolesRepo.insert(request.toBuilder().applicationId(321).build()) >>
                Mono.error(new DataIntegrityViolationException("test error"))

        expect:
        StepVerifier.create(service.create('app', request))
                .verifyError(DuplicateException)
    }

    def 'deleting a role should delete the role'() {
        PublisherProbe rolesProbe = PublisherProbe.empty()
        rolesRepo.delete(100) >> rolesProbe.mono()

        expect:
        StepVerifier.create(service.delete(100))
                .verifyComplete()
        rolesProbe.assertWasSubscribed()
    }

    def 'deleting a role by name should look up the role then delete the role'() {
        appsRepo.get('app') >> Mono.just(Application.builder().id(123).name('app').build())
        rolesRepo.get(123, 'role') >> Mono.just(Role.builder().id(456).name('role')
                .applicationId(123).build())
        PublisherProbe rolesProbe = PublisherProbe.empty()
        rolesRepo.delete(456) >> rolesProbe.mono()

        expect:
        StepVerifier.create(service.delete('app', 'role'))
                .verifyComplete()
        rolesProbe.assertWasSubscribed()
    }

    def 'should be able to retrieve a role by name'() {
        appsRepo.get('app-name') >> Mono.just(Application.builder().id(132).name('app-name').build())
        def role = Role.builder().id(123).name('role-name').applicationId(132).build()
        rolesRepo.get(132, 'role-name') >>
                Mono.just(Role.builder().id(123).name('role-name').applicationId(132).build())

        expect:
        StepVerifier.create(service.get('app-name', 'role-name'))
                .expectNext(role)
                .verifyComplete()

        StepVerifier.create(service.getRequired('app-name', 'role-name'))
                .expectNext(role)
                .verifyComplete()
    }

    def "should return an error when a required role's application does not exist"() {
        appsRepo.get('app-name') >> Mono.empty()

        expect:
        StepVerifier.create(service.getRequired('app-name', 'role-name'))
                .verifyError(ObjectNotFoundException)
    }

    def 'should return an error when a required role does not exist'() {
        appsRepo.get('app-name') >> Mono.just(Application.builder().id(132).name('app-name').build())
        rolesRepo.get(132, 'role-name') >> Mono.empty()

        expect:
        StepVerifier.create(service.getRequired('app-name', 'role-name'))
                .verifyError(ObjectNotFoundException)
    }

    def 'should be able to retrieve all roles'() {
        def role1 = Role.builder().id(1).applicationId(2).name('role1').build()
        def role2 = Role.builder().id(2).applicationId(3).name('role2').build()
        rolesRepo.getRoles(100, 0) >> Flux.just(role1, role2)

        expect:
        StepVerifier.create(service.getRoles(100, 0))
                .expectNext(role1, role2)
                .verifyComplete()
    }
}
