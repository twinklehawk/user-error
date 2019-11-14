package net.plshark.users.service

import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Group
import net.plshark.users.model.Role
import net.plshark.users.model.User
import net.plshark.users.repo.UserGroupsRepository
import net.plshark.users.repo.UserRolesRepository
import net.plshark.users.repo.UsersRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class UsersServiceImplSpec extends Specification {

    UsersRepository userRepo = Mock()
    UserRolesRepository userRolesRepo = Mock()
    UserGroupsRepository userGroupsRepo = Mock()
    RolesService rolesService = Mock()
    GroupsService groupsService = Mock()
    PasswordEncoder encoder = Mock()
    UsersServiceImpl service = new UsersServiceImpl(userRepo, userRolesRepo, userGroupsRepo, rolesService,
            groupsService, encoder)

    def "new users have password encoded"() {
        encoder.encode("pass") >> "pass-encoded"
        userRepo.insert(User.builder().username('user').password('pass-encoded').build()) >>
                Mono.just(User.builder().id(1L).username('user').build())

        expect:
        StepVerifier.create(service.create(User.builder().username('user').password('pass').build()))
                .expectNext(User.builder().id(1L).username('user').build())
                .verifyComplete()
    }

    def 'create should map the exception for a duplicate username to a DuplicateException'() {
        def request = User.builder().username('app').password('pass').build()
        encoder.encode('pass') >> 'pass'
        userRepo.insert(request) >> Mono.error(new DataIntegrityViolationException("test error"))

        expect:
        StepVerifier.create(service.create(request))
                .verifyError(DuplicateException)
    }

    def "cannot update a user to have null password"() {
        when:
        service.updateUserPassword('bill', "current", null)

        then:
        thrown(NullPointerException)
    }

    def "new password is encoded when updating password"() {
        encoder.encode("current") >> "current-encoded"
        encoder.encode("new-pass") >> "new-pass-encoded"
        userRepo.getForUsername('ted') >> Mono.just(User.builder().id(100).username('ted')
                .password('current-encoded').build())
        PublisherProbe probe = PublisherProbe.empty()
        userRepo.updatePassword(100, "current-encoded", "new-pass-encoded") >> probe.mono()

        expect:
        StepVerifier.create(service.updateUserPassword('ted', "current", "new-pass"))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "no matching user when updating password throws exception"() {
        encoder.encode("current") >> "current-encoded"
        encoder.encode("new") >> "new-encoded"
        userRepo.getForUsername('ted') >> Mono.empty()

        expect:
        StepVerifier.create(service.updateUserPassword('ted', "current", "new"))
            .verifyError(ObjectNotFoundException.class)
    }

    def "no matching existing password when updating password throws exception"() {
        encoder.encode("current") >> "current-encoded"
        encoder.encode("new") >> "new-encoded"
        userRepo.getForUsername('ted') >> Mono.just(User.builder().id(100).username('ted')
                .password('current-encoded').build())
        userRepo.updatePassword(100, "current-encoded", "new-encoded") >> Mono.error(new EmptyResultDataAccessException(1))

        expect:
        StepVerifier.create(service.updateUserPassword('ted', "current", "new"))
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
        StepVerifier.create(service.delete(100))
            .verifyComplete()
        rolesProbe.assertWasSubscribed()
        userProbe.assertWasSubscribed()
        groupsProbe.assertWasSubscribed()
    }

    def 'deleting by username retrieves the user, deletes all associations, and deletes the user'() {
        userRepo.getForUsername('user') >> Mono.just(User.builder().id(100).username('user').build())
        PublisherProbe rolesProbe = PublisherProbe.empty()
        userRolesRepo.deleteUserRolesForUser(100) >> rolesProbe.mono()
        PublisherProbe userProbe = PublisherProbe.empty()
        userRepo.delete(100) >> userProbe.mono()
        PublisherProbe groupsProbe = PublisherProbe.empty()
        userGroupsRepo.deleteUserGroupsForUser(100) >> groupsProbe.mono()

        expect:
        StepVerifier.create(service.delete(100))
                .verifyComplete()
        rolesProbe.assertWasSubscribed()
        userProbe.assertWasSubscribed()
        groupsProbe.assertWasSubscribed()
    }

    def "granting a role to a user should add the role to the user's roles"() {
        userRepo.getForUsername('bill') >> Mono.just(User.builder().id(12).username('bill')
                .password('pass').build())
        rolesService.getRequired('app', 'role') >> Mono.just(Role.builder().id(34)
                .applicationId(1).name('role').build())
        PublisherProbe probe = PublisherProbe.empty()
        userRolesRepo.insert(12, 34) >> probe.mono()

        expect:
        StepVerifier.create(service.grantRoleToUser('bill', 'app', 'role'))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "removing a role from a user should remove the role from the user's roles"() {
        userRepo.getForUsername('ted') >> Mono.just(User.builder().id(100).username('bill')
                .password('pass').build())
        rolesService.getRequired('app', 'role') >> Mono.just(Role.builder().id(200)
                .applicationId(1).name('role').build())
        PublisherProbe probe = PublisherProbe.empty()
        userRolesRepo.delete(100, 200) >> probe.mono()

        expect:
        StepVerifier.create(service.removeRoleFromUser('ted', 'app', 'role'))
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
        userRepo.getForUsername('bill') >> Mono.just(User.builder().id(100).username('bill')
                .password('pass').build())
        groupsService.getRequired('group') >> Mono.just(Group.create(200, 'group'))
        userGroupsRepo.insert(100, 200) >> Mono.empty()

        expect:
        StepVerifier.create(service.grantGroupToUser('bill', 'group'))
                .verifyComplete()
    }

    def 'should be able to remove a user from a group'() {
        userRepo.getForUsername('ted') >> Mono.just(User.builder().id(100).username('ted')
                .password('pass').build())
        groupsService.getRequired('group') >> Mono.just(Group.create(200, 'group'))
        userGroupsRepo.delete(100, 200) >> Mono.empty()

        expect:
        StepVerifier.create(service.removeGroupFromUser('ted', 'group'))
                .verifyComplete()
    }
}
