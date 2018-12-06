package net.plshark.users.webservice

import net.plshark.users.model.Role
import net.plshark.users.service.UserManagementService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class RolesControllerSpec extends Specification {

    UserManagementService service = Mock()
    RolesController controller = new RolesController(service)

    def "insert passes role through to service"() {
        service.insertRole({ Role role -> role.id == null && role.name == "admin" }) >> Mono.just(new Role(100, "admin"))

        expect:
        StepVerifier.create(controller.insert(new Role("admin")))
            .expectNext(new Role(100, "admin"))
            .verifyComplete()
    }

    def "delete passes ID through to service"() {
        PublisherProbe probe = PublisherProbe.empty()
        service.deleteRole(100) >> probe.mono()

        expect:
        StepVerifier.create(controller.delete(100))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def 'getRoles passes the max results and offset through'() {
        def role1 = new Role('role1')
        def role2 = new Role('role2')
        service.getRoles(3, 2) >> Flux.just(role1, role2)

        expect:
        StepVerifier.create(controller.getRoles(3, 2))
                .expectNext(role1, role2)
                .verifyComplete()
    }

    def 'getByName passes the role name through'() {
        def role1 = new Role('role')
        service.getRoleByName('role') >> Mono.just(role1)

        expect:
        StepVerifier.create(controller.getByName('role'))
                .expectNext(role1)
                .verifyComplete()
    }
}
