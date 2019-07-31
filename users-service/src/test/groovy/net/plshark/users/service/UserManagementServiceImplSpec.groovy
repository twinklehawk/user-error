package net.plshark.users.service

import net.plshark.users.model.UserInfo
import net.plshark.users.repo.RolesRepository
import net.plshark.users.repo.UserRolesRepository
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.crypto.password.PasswordEncoder

import net.plshark.ObjectNotFoundException
import net.plshark.users.model.Role
import net.plshark.users.model.User
import net.plshark.users.repo.UsersRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe
import spock.lang.Specification

class UserManagementServiceImplSpec extends Specification {

    UsersRepository userRepo = Mock()
    RolesRepository roleRepo = Mock()
    UserRolesRepository userRolesRepo = Mock()
    PasswordEncoder encoder = Mock()
    UserManagementServiceImpl service = new UserManagementServiceImpl(userRepo, roleRepo, userRolesRepo, encoder)

    def "new users have password encoded"() {
        encoder.encode("pass") >> "pass-encoded"
        userRepo.insert(new User("user", "pass-encoded")) >> Mono.just(new User(1L, 'user', 'pass-encoded'))

        expect:
        StepVerifier.create(service.insertUser(new User("user", "pass")))
                .expectNext(new UserInfo(1L, 'user'))
                .verifyComplete()
    }

    def "saving a role passes role through"() {
        when:
        service.insertRole(Role.create("name", "application"))

        then:
        1 * roleRepo.insert(Role.create("name", "application"))
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

    def "all roles for a user are removed when the user is deleted"() {
        PublisherProbe rolesProbe = PublisherProbe.empty()
        userRolesRepo.deleteUserRolesForUser(100) >> rolesProbe.mono()
        PublisherProbe userProbe = PublisherProbe.empty()
        userRepo.delete(100) >> userProbe.mono()

        expect:
        StepVerifier.create(service.deleteUser(100))
            .verifyComplete()
        rolesProbe.assertWasSubscribed()
        rolesProbe.assertWasRequested()
        rolesProbe.assertWasNotCancelled()
        userProbe.assertWasSubscribed()
        userProbe.assertWasRequested()
        userProbe.assertWasNotCancelled()
    }

    def "role is removed from all users when the role is deleted"() {
        PublisherProbe userRolesProbe = PublisherProbe.empty()
        userRolesRepo.deleteUserRolesForRole(200) >> userRolesProbe.mono()
        PublisherProbe userProbe = PublisherProbe.empty()
        roleRepo.delete(200) >> userProbe.mono()

        expect:
        StepVerifier.create(service.deleteRole(200))
            .verifyComplete()
        userRolesProbe.assertWasSubscribed()
        userRolesProbe.assertWasRequested()
        userRolesProbe.assertWasNotCancelled()
        userProbe.assertWasSubscribed()
        userProbe.assertWasRequested()
        userProbe.assertWasNotCancelled()
    }

    def "granting a role to a user should add the role to the user's role"() {
        userRepo.getForId(12) >> Mono.just(new User("name", "pass"))
        roleRepo.getForId(34) >> Mono.just(Role.create("role", "application"))
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
        userRepo.getForId(100) >> Mono.just(new User("name", "pass"))
        PublisherProbe probe = PublisherProbe.empty()
        userRolesRepo.deleteUserRole(100, 200) >> probe.mono()

        expect:
        StepVerifier.create(service.removeRoleFromUser(100, 200))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "retrieving a role by name passes the name through"() {
        roleRepo.getForName("name") >> Mono.just(Role.create(1, "name", "application"))

        expect:
        StepVerifier.create(service.getRoleByName("name"))
            .expectNext(Role.create(1, "name", "application"))
            .verifyComplete()
    }

    def "an empty optional is returned when no role matches the name"() {
        roleRepo.getForName("name") >> Mono.empty()

        expect:
        StepVerifier.create(service.getRoleByName("name"))
            .verifyComplete()
    }

    def 'getUsers should return all results'() {
        def user1 = new User(1L, 'user', 'pass')
        def user2 = new User(2L, 'user2', 'pass')
        userRepo.getAll(5, 0) >> Flux.just(user1, user2)

        expect:
        StepVerifier.create(service.getUsers(5, 0))
                .expectNext(new UserInfo(1L, 'user'), new UserInfo(2L, 'user2'))
                .verifyComplete()
    }

    def 'getRoles should return all results'() {
        def role1 = Role.create(1L, 'role1', 'test-app')
        def role2 = Role.create(2L, 'role2', 'test-app')
        roleRepo.getRoles(5, 0) >> Flux.just(role1, role2)

        expect:
        StepVerifier.create(service.getRoles(5, 0))
                .expectNext(role1, role2)
                .verifyComplete()
    }
}
