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
        service.insertRole({ Role role -> role.id == null && role.name == "admin" }) >>
                Mono.just(Role.create(100, "admin", "app"))

        expect:
        StepVerifier.create(controller.insert(Role.create("admin", "app")))
            .expectNext(Role.create(100, "admin", "app"))
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
        def role1 = Role.create('role1', 'app')
        def role2 = Role.create('role2', 'app')
        service.getRoles(3, 2) >> Flux.just(role1, role2)

        expect:
        StepVerifier.create(controller.getRoles(3, 2))
                .expectNext(role1, role2)
                .verifyComplete()
    }

    def 'getByName passes the role name through'() {
        def role1 = Role.create('role', 'app')
        service.getRoleByName('role') >> Mono.just(role1)

        expect:
        StepVerifier.create(controller.getByName('role'))
                .expectNext(role1)
                .verifyComplete()
    }
}
