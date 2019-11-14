package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import net.plshark.testutils.PlsharkFlywayPreparer
import net.plshark.users.model.Application
import net.plshark.users.model.Role
import net.plshark.users.model.User
import org.junit.Rule
import spock.lang.Specification

class SpringDataUserRolesRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SpringDataUserRolesRepository repo
    SpringDataUsersRepository usersRepo
    SpringDataRolesRepository rolesRepo
    SpringDataApplicationsRepository appsRepo
    Role testRole1
    Role testRole2
    User user1
    User user2

    def setup() {
        def client = DatabaseClientHelper.buildTestClient(dbRule)
        repo = new SpringDataUserRolesRepository(client)
        usersRepo = new SpringDataUsersRepository(client)
        rolesRepo = new SpringDataRolesRepository(client)
        appsRepo = new SpringDataApplicationsRepository(client)

        def app = appsRepo.insert(Application.builder().name('app').build()).block()
        testRole1 = rolesRepo.insert(Role.builder().name('testRole1').applicationId(app.id).build()).block()
        testRole2 = rolesRepo.insert(Role.builder().name('testRole2').applicationId(app.id).build()).block()
        user1 = usersRepo.insert(User.builder().username('test-user').password('test-pass').build()).block()
        user2 = usersRepo.insert(User.builder().username('test-user2').password('test-pass').build()).block()
    }

    def "can add a role to a user"() {
        when:
        repo.insert(user1.id, testRole1.id).block()

        then:
        repo.getRolesForUser(user1.id).collectList().block().stream().anyMatch{role -> role.id == testRole1.id}
    }

    def "can retrieve all roles for a user"() {
        repo.insert(user1.id, testRole1.id).block()
        repo.insert(user1.id, testRole2.id).block()

        when:
        List<Role> roles = repo.getRolesForUser(user1.id).collectList().block()

        then:
        roles.size() == 2
        roles.stream().anyMatch{role -> role.id == testRole1.id}
        roles.stream().anyMatch{role -> role.id == testRole2.id}
    }

    def "retrieving roles for a user does not return roles for other users"() {
        repo.insert(user1.id, testRole1.id).block()
        repo.insert(user2.id, testRole2.id).block()

        when:
        List<Role> roles = repo.getRolesForUser(user1.id).collectList().block()

        then:
        roles.size() == 1
        roles.stream().anyMatch{role -> role.id == testRole1.id}
    }

    def "can delete an existing user role"() {
        repo.insert(user1.id, testRole1.id).block()

        when:
        repo.delete(user1.id, testRole1.id).block()

        then:
        repo.getRolesForUser(user1.id).collectList().block().size() == 0
    }

    def "deleting a user role that does not exist does not throw an exception"() {
        when:
        repo.delete(user1.id, 200).block()

        then:
        notThrown(Exception)
    }

    def "can delete all roles for a user"() {
        repo.insert(user1.id, testRole1.id).block()
        repo.insert(user1.id, testRole2.id).block()

        when:
        repo.deleteUserRolesForUser(user1.id).block()

        then:
        repo.getRolesForUser(user1.id).collectList().block().size() == 0
    }

    def "deleting all roles for a user does not affect other users"() {
        repo.insert(user1.id, testRole1.id).block()
        repo.insert(user2.id, testRole2.id).block()

        when:
        repo.deleteUserRolesForUser(user1.id).block()

        then:
        repo.getRolesForUser(user1.id).collectList().block().size() == 0
        repo.getRolesForUser(user2.id).collectList().block().size() == 1
    }

    def "can remove a role from all users"() {
        repo.insert(user1.id, testRole1.id).block()
        repo.insert(user2.id, testRole1.id).block()

        when:
        repo.deleteUserRolesForRole(testRole1.id).block()

        then:
        repo.getRolesForUser(user1.id).collectList().block().size() == 0
        repo.getRolesForUser(user2.id).collectList().block().size() == 0
    }

    def "removing a role from all users does not affect other roles"() {
        repo.insert(user1.id, testRole1.id).block()
        repo.insert(user2.id, testRole2.id).block()

        when:
        repo.deleteUserRolesForRole(testRole1.id).block()

        then:
        repo.getRolesForUser(user1.id).collectList().block().size() == 0
        repo.getRolesForUser(user2.id).collectList().block().size() == 1
    }
}
