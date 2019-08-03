package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import net.plshark.testutils.PlsharkFlywayPreparer
import net.plshark.users.model.Role
import net.plshark.users.model.User
import org.junit.Rule
import org.springframework.data.r2dbc.core.DatabaseClient
import spock.lang.Specification

class SpringDataUserRolesRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SpringDataUserRolesRepository repo
    SpringDataUsersRepository usersRepo
    SpringDataRolesRepository rolesRepo
    Role testRole1
    Role testRole2
    User user1
    User user2

    def setup() {
        def client = DatabaseClient.create(
                new PostgresqlConnectionFactory(
                        PostgresqlConnectionConfiguration.builder()
                                .database(dbRule.connectionInfo.dbName)
                                .host('localhost')
                                .port(dbRule.connectionInfo.port)
                                .username(dbRule.connectionInfo.user)
                                .password('')
                                .build()))
        repo = new SpringDataUserRolesRepository(client)
        usersRepo = new SpringDataUsersRepository(client)
        rolesRepo = new SpringDataRolesRepository(client)

        testRole1 = rolesRepo.insert(Role.create("testRole1", "app")).block()
        testRole2 = rolesRepo.insert(Role.create("testRole2", "app")).block()
        user1 = usersRepo.insert(User.create('test-user', 'test-pass')).block()
        user2 = usersRepo.insert(User.create('test-user2', 'test-pass')).block()
    }

    def "can add a role to a user"() {
        when:
        repo.insertUserRole(user1.id, testRole1.id).block()

        then:
        repo.getRolesForUser(user1.id).collectList().block().stream().anyMatch{role -> role.id == testRole1.id}
    }

    def "can retrieve all roles for a user"() {
        repo.insertUserRole(user1.id, testRole1.id).block()
        repo.insertUserRole(user1.id, testRole2.id).block()

        when:
        List<Role> roles = repo.getRolesForUser(user1.id).collectList().block()

        then:
        roles.size() == 2
        roles.stream().anyMatch{role -> role.id == testRole1.id}
        roles.stream().anyMatch{role -> role.id == testRole2.id}
    }

    def "retrieving roles for a user does not return roles for other users"() {
        repo.insertUserRole(user1.id, testRole1.id).block()
        repo.insertUserRole(user2.id, testRole2.id).block()

        when:
        List<Role> roles = repo.getRolesForUser(user1.id).collectList().block()

        then:
        roles.size() == 1
        roles.stream().anyMatch{role -> role.id == testRole1.id}
    }

    def "can delete an existing user role"() {
        repo.insertUserRole(user1.id, testRole1.id).block()

        when:
        repo.deleteUserRole(user1.id, testRole1.id).block()

        then:
        repo.getRolesForUser(user1.id).collectList().block().size() == 0
    }

    def "deleting a user role that does not exist does not throw an exception"() {
        when:
        repo.deleteUserRole(user1.id, 200).block()

        then:
        notThrown(Exception)
    }

    def "can delete all roles for a user"() {
        repo.insertUserRole(user1.id, testRole1.id).block()
        repo.insertUserRole(user1.id, testRole2.id).block()

        when:
        repo.deleteUserRolesForUser(user1.id).block()

        then:
        repo.getRolesForUser(user1.id).collectList().block().size() == 0
    }

    def "deleting all roles for a user does not affect other users"() {
        repo.insertUserRole(user1.id, testRole1.id).block()
        repo.insertUserRole(user2.id, testRole2.id).block()

        when:
        repo.deleteUserRolesForUser(user1.id).block()

        then:
        repo.getRolesForUser(user1.id).collectList().block().size() == 0
        repo.getRolesForUser(user2.id).collectList().block().size() == 1
    }

    def "can remove a role from all users"() {
        repo.insertUserRole(user1.id, testRole1.id).block()
        repo.insertUserRole(user2.id, testRole1.id).block()

        when:
        repo.deleteUserRolesForRole(testRole1.id).block()

        then:
        repo.getRolesForUser(user1.id).collectList().block().size() == 0
        repo.getRolesForUser(user2.id).collectList().block().size() == 0
    }

    def "removing a role from all users does not affect other roles"() {
        repo.insertUserRole(user1.id, testRole1.id).block()
        repo.insertUserRole(user2.id, testRole2.id).block()

        when:
        repo.deleteUserRolesForRole(testRole1.id).block()

        then:
        repo.getRolesForUser(user1.id).collectList().block().size() == 0
        repo.getRolesForUser(user2.id).collectList().block().size() == 1
    }
}
