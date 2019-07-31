package net.plshark.users.repo.jdbc

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import net.plshark.testutils.PlsharkFlywayPreparer
import net.plshark.users.model.Role
import net.plshark.users.model.User
import org.junit.Rule
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Specification

class SyncJdbcUserRolesRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SyncJdbcUserRolesRepository repo
    SyncJdbcUsersRepository usersRepo
    SyncJdbcRolesRepository rolesRepo
    Role testRole1
    Role testRole2
    User user1
    User user2

    def setup() {
        JdbcTemplate template = new JdbcTemplate(dbRule.testDatabase)
        repo = new SyncJdbcUserRolesRepository(template)
        rolesRepo = new SyncJdbcRolesRepository(template)
        usersRepo = new SyncJdbcUsersRepository(template)

        testRole1 = rolesRepo.insert(Role.create("testRole1", "app"))
        testRole2 = rolesRepo.insert(Role.create("testRole2", "app"))
        user1 = usersRepo.insert(new User('test-user', 'test-pass'))
        user2 = usersRepo.insert(new User('test-user2', 'test-pass'))
    }

    def "can add a role to a user"() {
        when:
        repo.insertUserRole(user1.id, testRole1.id)

        then:
        repo.getRolesForUser(user1.id).stream().anyMatch{role -> role.id == testRole1.id}
    }

    def "can retrieve all roles for a user"() {
        repo.insertUserRole(user1.id, testRole1.id)
        repo.insertUserRole(user1.id, testRole2.id)

        when:
        List<Role> roles = repo.getRolesForUser(user1.id)

        then:
        roles.size() == 2
        roles.stream().anyMatch{role -> role.id == testRole1.id}
        roles.stream().anyMatch{role -> role.id == testRole2.id}
    }

    def "retrieving roles for a user does not return roles for other users"() {
        repo.insertUserRole(user1.id, testRole1.id)
        repo.insertUserRole(user2.id, testRole2.id)

        when:
        List<Role> roles = repo.getRolesForUser(user1.id)

        then:
        roles.size() == 1
        roles.stream().anyMatch{role -> role.id == testRole1.id}
    }

    def "can delete an existing user role"() {
        repo.insertUserRole(user1.id, testRole1.id)

        when:
        repo.deleteUserRole(user1.id, testRole1.id)

        then:
        repo.getRolesForUser(user1.id).size() == 0
    }

    def "deleting a user role that does not exist does not throw an exception"() {
        when:
        repo.deleteUserRole(user1.id, 200)

        then:
        notThrown(Exception)
    }

    def "can delete all roles for a user"() {
        repo.insertUserRole(user1.id, testRole1.id)
        repo.insertUserRole(user1.id, testRole2.id)

        when:
        repo.deleteUserRolesForUser(user1.id)

        then:
        repo.getRolesForUser(user1.id).size() == 0
    }

    def "deleting all roles for a user does not affect other users"() {
        repo.insertUserRole(user1.id, testRole1.id)
        repo.insertUserRole(user2.id, testRole2.id)

        when:
        repo.deleteUserRolesForUser(user1.id)

        then:
        repo.getRolesForUser(user1.id).size() == 0
        repo.getRolesForUser(user2.id).size() == 1
    }

    def "can remove a role from all users"() {
        repo.insertUserRole(user1.id, testRole1.id)
        repo.insertUserRole(user2.id, testRole1.id)

        when:
        repo.deleteUserRolesForRole(testRole1.id)

        then:
        repo.getRolesForUser(user1.id).size() == 0
        repo.getRolesForUser(user2.id).size() == 0
    }

    def "removing a role from all users does not affect other roles"() {
        repo.insertUserRole(user1.id, testRole1.id)
        repo.insertUserRole(user2.id, testRole2.id)

        when:
        repo.deleteUserRolesForRole(testRole1.id)

        then:
        repo.getRolesForUser(user1.id).size() == 0
        repo.getRolesForUser(user2.id).size() == 1
    }
}
