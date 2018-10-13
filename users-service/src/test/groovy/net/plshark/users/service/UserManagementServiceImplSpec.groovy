package net.plshark.users.service

import net.plshark.users.repo.RolesRepository
import net.plshark.users.repo.UserRolesRepository
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.crypto.password.PasswordEncoder

import net.plshark.ObjectNotFoundException
import net.plshark.users.model.Role
import net.plshark.users.model.User
import net.plshark.users.repo.UsersRepository
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

    def "cannot save user with ID set"() {
        when:
        service.saveUser(new User(12, "name", "pass"))

        then:
        thrown(IllegalArgumentException)
    }

    def "new users have password encoded"() {
        encoder.encode("pass") >> "pass-encoded"

        when:
        service.saveUser(new User("user", "pass"))

        then:
        1 * userRepo.insert(new User("user", "pass-encoded"))
    }

    def "cannot save role with ID set"() {
        when:
        service.saveRole(new Role(1, "name"))

        then:
        thrown(IllegalArgumentException)
    }

    def "saving a role passes role through"() {
        when:
        service.saveRole(new Role("name"))

        then:
        1 * roleRepo.insert(new Role("name"))
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
        roleRepo.getForId(34) >> Mono.just(new Role("role"))
        PublisherProbe probe = PublisherProbe.empty()
        userRolesRepo.insertUserRole(12, 34) >> probe.mono()

        expect:
        StepVerifier.create(service.grantRoleToUser(12, 34))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    def "granting a role to a user that does not exist should throw an ObjectNotFoundException"() {
        userRepo.getForId(100) >> Mono.empty()
        roleRepo.getForId(200) >> Mono.just(new Role("role"))

        expect:
        StepVerifier.create(service.grantRoleToUser(100, 200))
            .verifyError(ObjectNotFoundException.class)
    }

    def "granting a role that does not exist should throw an ObjectNotFoundException"() {
        userRepo.getForId(100) >> Mono.just(new User("name", "pass"))
        roleRepo.getForId(200) >> Mono.empty()

        expect:
        StepVerifier.create(service.grantRoleToUser(100, 200))
            .verifyError(ObjectNotFoundException.class)
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

    def "removing a role from a user that does not exist should throw an ObjectNotFoundException"() {
        userRepo.getForId(100) >> Mono.empty()

        expect:
        StepVerifier.create(service.removeRoleFromUser(100, 200))
            .verifyError(ObjectNotFoundException.class)
    }

    def "retrieving a role by name passes the name through"() {
        roleRepo.getForName("name") >> Mono.just(new Role(1, "name"))

        expect:
        StepVerifier.create(service.getRoleByName("name"))
            .expectNext(new Role(1, "name"))
            .verifyComplete()
    }

    def "an empty optional is returned when no role matches the name"() {
        roleRepo.getForName("name") >> Mono.empty()

        expect:
        StepVerifier.create(service.getRoleByName("name"))
            .verifyComplete()
    }
}
