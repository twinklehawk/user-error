package net.plshark.users.webservice

import net.plshark.errors.BadRequestException
import net.plshark.users.model.PasswordChangeRequest
import net.plshark.users.model.RoleGrant
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
        service.delete('user') >> probe.mono()

        expect:
        StepVerifier.create(controller.delete('user'))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "change password passes the user ID, current password, and new passwords through"() {
        PublisherProbe probe = PublisherProbe.empty()
        service.updateUserPassword('bob', "current", "new") >> probe.mono()

        expect:
        StepVerifier.create(controller.changePassword('bob', PasswordChangeRequest.create("current", "new")))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "granting a role passes the user and role names through"() {
        PublisherProbe probe = PublisherProbe.empty()
        service.grantRoleToUser('user', 'test-app', 'role1') >> probe.mono()

        expect:
        StepVerifier.create(controller.grantRole('user', RoleGrant.create('test-app', 'role1')))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "removing a role passes the user and role names through"() {
        PublisherProbe probe = PublisherProbe.empty()
        service.removeRoleFromUser('ted', 'app', 'role') >> probe.mono()

        expect:
        StepVerifier.create(controller.removeRole('ted', 'app', 'role'))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "granting a group passes the user and group names through"() {
        PublisherProbe probe = PublisherProbe.empty()
        service.grantGroupToUser('user', 'group') >> probe.mono()

        expect:
        StepVerifier.create(controller.grantGroup('user', 'group'))
                .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "removing a group passes the user and role names through"() {
        PublisherProbe probe = PublisherProbe.empty()
        service.removeGroupFromUser('ted', 'group') >> probe.mono()

        expect:
        StepVerifier.create(controller.removeGroup('ted', 'group'))
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
        service.get('user') >> Mono.just(user1)

        expect:
        StepVerifier.create(controller.getUser('user'))
                .expectNext(user1)
                .verifyComplete()
    }

    def 'insert passes through the response from the service'() {
        def request = User.builder().username('user').password('test-pass').build()
        def created = User.builder().id(1L).username('user').build()
        service.create(request) >> Mono.just(created)

        expect:
        StepVerifier.create(controller.create(request))
                .expectNext(created)
                .verifyComplete()
    }

    def 'an insert request is rejected if the password is empty'() {
        def request = User.builder().username('user').build()

        when:
        controller.create(request)

        then:
        thrown(BadRequestException)
    }
}
