package net.plshark.users.webservice

import net.plshark.BadRequestException
import net.plshark.users.Role
import net.plshark.users.service.UserManagementService
import net.plshark.users.webservice.RolesController
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class RolesControllerSpec extends Specification {

    UserManagementService service = Mock()
    RolesController controller = new RolesController(service)

    def "constructor does not accept null args"() {
        when:
        new RolesController(null)

        then:
        thrown(NullPointerException)
    }

    def "cannot insert a role with ID already set"() {
        expect:
        StepVerifier.create(controller.insert(new Role(1, "name")))
            .verifyError(BadRequestException)
    }

    def "insert passes role through to service"() {
        service.saveRole({ Role role -> !role.id.present && role.name == "admin" }) >> Mono.just(new Role(100, "admin"))

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
}
