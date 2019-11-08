package net.plshark.users.webservice

import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Role
import net.plshark.users.service.RolesService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class RolesControllerSpec extends Specification {

    RolesService service = Mock()
    RolesController controller = new RolesController(service)

    def "insert passes role through to service"() {
        def request = Role.builder().name('admin').build()
        def inserted = Role.builder().id(100).name('app').applicationId(12).build()
        service.insert('app', request) >> Mono.just(inserted)

        expect:
        StepVerifier.create(controller.insert('app', request))
            .expectNext(inserted)
            .verifyComplete()
    }

    def 'getting a role should throw an exception when the role does not exist'() {
        service.get('test-app', 'test-role') >> Mono.empty()

        expect:
        StepVerifier.create(controller.get('test-app', 'test-role'))
                .verifyError(ObjectNotFoundException)
    }

    def "delete passes ID through to service"() {
        PublisherProbe probe = PublisherProbe.empty()
        service.delete('app', 'role') >> probe.mono()

        expect:
        StepVerifier.create(controller.delete('app', 'role'))
            .verifyComplete()
        probe.assertWasSubscribed()
    }

    def 'getRoles passes the max results and offset through'() {
        def role1 = Role.builder().name('role1').applicationId(1).build()
        def role2 = Role.builder().name('role2').applicationId(1).build()
        service.getRoles(3, 2) >> Flux.just(role1, role2)

        expect:
        StepVerifier.create(controller.getRoles(3, 2))
                .expectNext(role1, role2)
                .verifyComplete()
    }

    def 'get passes the role name through'() {
        def role1 = Role.builder().name('role').applicationId(1).build()
        service.get('app', 'role') >> Mono.just(role1)

        expect:
        StepVerifier.create(controller.get('app', 'role'))
                .expectNext(role1)
                .verifyComplete()
    }
}
