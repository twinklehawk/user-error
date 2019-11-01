package net.plshark.users.webservice

import net.plshark.users.model.PasswordChangeRequest
import net.plshark.users.model.User
import net.plshark.users.service.UsersService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class UsersControllerSpec extends Specification {

    UsersService service = Mock()
    UsersController controller = new UsersController(service)

    def "delete passes the user ID through to be deleted"() {
        PublisherProbe probe = PublisherProbe.empty()
        service.deleteUser('user') >> probe.mono()

        expect:
        StepVerifier.create(controller.delete('user'))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "change password passes the user ID, current password, and new passwords through"() {
        PublisherProbe probe = PublisherProbe.empty()
        service.updateUserPassword(100, "current", "new") >> probe.mono()

        expect:
        StepVerifier.create(controller.changePassword(100, PasswordChangeRequest.create("current", "new")))
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

    def 'getUsers passes the max results and offset through'() {
        def user1 = User.builder().id(1L).username('user').build()
        def user2 = User.builder().id(2L).username('user2').build()
        service.getUsers(3, 2) >> Flux.just(user1, user2)

        expect:
        StepVerifier.create(controller.getUsers(3, 2))
                .expectNext(user1, user2)
                .verifyComplete()
    }

    def 'getUser passes the username through'() {
        def user1 = User.builder().id(1L).username('user').build()
        service.getUserByUsername('user') >> Mono.just(user1)

        expect:
        StepVerifier.create(controller.getUser('user'))
                .expectNext(user1)
                .verifyComplete()
    }
}
