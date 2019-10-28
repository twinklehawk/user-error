package net.plshark.users.service

import net.plshark.ObjectNotFoundException
import net.plshark.users.model.User
import net.plshark.users.repo.UserGroupsRepository
import net.plshark.users.repo.UserRolesRepository
import net.plshark.users.repo.UsersRepository
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class UserManagementServiceImplSpec extends Specification {

    UsersRepository userRepo = Mock()
    UserRolesRepository userRolesRepo = Mock()
    UserGroupsRepository userGroupsRepo = Mock()
    PasswordEncoder encoder = Mock()
    UserManagementServiceImpl service = new UserManagementServiceImpl(userRepo, userRolesRepo, userGroupsRepo, encoder)

    def "new users have password encoded"() {
        encoder.encode("pass") >> "pass-encoded"
        userRepo.insert(User.builder().username('user').password('pass-encoded').build()) >>
                Mono.just(User.builder().id(1L).username('user').build())

        expect:
        StepVerifier.create(service.insertUser(User.builder().username('user').password('pass').build()))
                .expectNext(User.builder().id(1L).username('user').build())
                .verifyComplete()
    }

    def "cannot update a user to have null password"() {
        when:
        service.updateUserPassword(100, "current", null)

        then:
        thrown(NullPointerException)
    }

    def "new password is encoded when updating password"() {
        encoder.encode("current") >> "current-encoded"
        encoder.encode("new-pass") >> "new-pass-encoded"
        PublisherProbe probe = PublisherProbe.empty()
        userRepo.updatePassword(100, "current-encoded", "new-pass-encoded") >> probe.mono()

        expect:
        StepVerifier.create(service.updateUserPassword(100, "current", "new-pass"))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "no matching user when updating password throws exception"() {
        encoder.encode("current") >> "current-encoded"
        encoder.encode("new") >> "new-encoded"
        userRepo.updatePassword(100, "current-encoded", "new-encoded") >> Mono.error(new EmptyResultDataAccessException(1))

        expect:
        StepVerifier.create(service.updateUserPassword(100, "current", "new"))
            .verifyError(ObjectNotFoundException.class)
    }

    def "all roles and groups for a user are removed when the user is deleted"() {
        PublisherProbe rolesProbe = PublisherProbe.empty()
        userRolesRepo.deleteUserRolesForUser(100) >> rolesProbe.mono()
        PublisherProbe userProbe = PublisherProbe.empty()
        userRepo.delete(100) >> userProbe.mono()
        PublisherProbe groupsProbe = PublisherProbe.empty()
        userGroupsRepo.deleteUserGroupsForUser(100) >> groupsProbe.mono()

        expect:
        StepVerifier.create(service.deleteUser(100))
            .verifyComplete()
        rolesProbe.assertWasSubscribed()
        userProbe.assertWasSubscribed()
        groupsProbe.assertWasSubscribed()
    }

    def "granting a role to a user should add the role to the user's roles"() {
        PublisherProbe probe = PublisherProbe.empty()
        userRolesRepo.insertUserRole(12, 34) >> probe.mono()

        expect:
        StepVerifier.create(service.grantRoleToUser(12, 34))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "removing a role from a user should remove the role from the user's roles"() {
        userRepo.getForId(100) >> Mono.just(User.builder().username('name').password('pass').build())
        PublisherProbe probe = PublisherProbe.empty()
        userRolesRepo.deleteUserRole(100, 200) >> probe.mono()

        expect:
        StepVerifier.create(service.removeRoleFromUser(100, 200))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def 'getUsers should return all results'() {
        def user1 = User.builder().id(1L).username('user').build()
        def user2 = User.builder().id(2L).username('user2').build()
        userRepo.getAll(5, 0) >> Flux.just(user1, user2)

        expect:
        StepVerifier.create(service.getUsers(5, 0))
                .expectNext(user1, user2)
                .verifyComplete()
    }

    def 'should be able to add a user to a group'() {
        userGroupsRepo.insert(100, 200) >> Mono.empty()

        expect:
        StepVerifier.create(service.addUserToGroup(100, 200))
                .verifyComplete()
    }

    def 'should be able to remove a user from a group'() {
        userGroupsRepo.delete(100, 200) >> Mono.empty()

        expect:
        StepVerifier.create(service.removeUserFromGroup(100, 200))
                .verifyComplete()
    }
}
