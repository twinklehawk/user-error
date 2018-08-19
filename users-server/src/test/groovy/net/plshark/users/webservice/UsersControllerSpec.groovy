package net.plshark.users.webservice

import net.plshark.BadRequestException
import net.plshark.users.service.UserManagementService
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class UsersControllerSpec extends Specification {

    UserManagementService service = Mock()
    UsersController controller = new UsersController(service)

    def "constructor does not accept null args"() {
        when:
        new UsersController(null)

        then:
        thrown(NullPointerException)
    }

    def "inserting a user with an ID throws BadRequestException"() {
        when:
        controller.insert(new net.plshark.users.model.User(1, "name", "pass"))

        then:
        thrown(BadRequestException)
    }

    def "password is not returned when creating a user"() {
        service.saveUser(_) >> Mono.just(new net.plshark.users.model.User(1, "user", "pass-encoded"))

        expect:
        StepVerifier.create(controller.insert(new net.plshark.users.model.User("name", "pass")))
            .expectNextMatches({ net.plshark.users.model.User user -> user.password.present == false})
            .verifyComplete()
    }

    def "delete passes the user ID through to be deleted"() {
        PublisherProbe probe = PublisherProbe.empty()
        service.deleteUser(100) >> probe.mono()

        expect:
        StepVerifier.create(controller.delete(100))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "change password passes the user ID, current password, and new passwords through"() {
        PublisherProbe probe = PublisherProbe.empty()
        service.updateUserPassword(100, "current", "new") >> probe.mono()

        expect:
        StepVerifier.create(controller.changePassword(100, net.plshark.users.model.PasswordChangeRequest.create("current", "new")))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "granting a role passes the user and role IDs through"() {
        PublisherProbe probe = PublisherProbe.empty()
        service.grantRoleToUser(100, 200) >> probe.mono()

        expect:
        StepVerifier.create(controller.grantRole(100, 200))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "removing a role passes the user and role IDs through"() {
        PublisherProbe probe = PublisherProbe.empty()
        service.removeRoleFromUser(200, 300) >> probe.mono()

        expect:
        StepVerifier.create(controller.removeRole(200, 300))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }
}
