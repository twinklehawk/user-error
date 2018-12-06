package net.plshark.users.webservice

import net.plshark.users.model.PasswordChangeRequest
import net.plshark.users.model.User
import net.plshark.users.model.UserInfo
import net.plshark.users.service.UserManagementService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class UsersControllerSpec extends Specification {

    UserManagementService service = Mock()
    UsersController controller = new UsersController(service)

    def "password is not returned when creating a user"() {
        service.insertUser(_) >> Mono.just(new User(1, "user", "pass-encoded"))

        expect:
        StepVerifier.create(controller.insert(new User("name", "pass")))
            .expectNextMatches({ UserInfo user -> user.id == 1 && user.username == 'user' })
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
        StepVerifier.create(controller.changePassword(100, new PasswordChangeRequest("current", "new")))
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
        def user1 = new User(1L, 'user', 'pass')
        def user2 = new User(2L, 'user2', 'pass2')
        service.getUsers(3, 2) >> Flux.just(user1, user2)

        expect:
        StepVerifier.create(controller.getUsers(3, 2))
                .expectNext(new UserInfo(1, 'user'), new UserInfo(2, 'user2'))
                .verifyComplete()
    }

    def 'getUser passes the username through'() {
        def user1 = new User(1L, 'user', 'pass')
        service.getUserByUsername('user') >> Mono.just(user1)

        expect:
        StepVerifier.create(controller.getUser('user'))
                .expectNext(new UserInfo(1, 'user'))
                .verifyComplete()
    }
}
