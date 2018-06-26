package net.plshark.users.repo.jdbc

import javax.inject.Inject

import org.springframework.boot.test.context.SpringBootTest

import net.plshark.jdbc.RepoTestConfig
import net.plshark.users.Role
import spock.lang.Specification

@SpringBootTest(classes = RepoTestConfig.class)
class SyncJdbcUserRolesRepositoryIntSpec extends Specification {

    @Inject
    SyncJdbcUserRolesRepository repo
    Role testRole1
    Role testRole2
    @Inject
    SyncJdbcRolesRepository rolesRepo

    def setup() {
        testRole1 = rolesRepo.insert(new Role("testRole1"))
        testRole2 = rolesRepo.insert(new Role("testRole2"))
    }

    def cleanup() {
        if (testRole1 != null)
            rolesRepo.delete(testRole1.id.get())
        if (testRole2 != null)
            rolesRepo.delete(testRole2.id.get())
    }

    def "can add a role to a user"() {
        when:
        repo.insertUserRole(100, testRole1.id.get())

        then:
        repo.getRolesForUser(100).stream().anyMatch{role -> role.id.get() == testRole1.id.get()}

        cleanup:
        repo.deleteUserRolesForUser(100)
    }

    def "can retrieve all roles for a user"() {
        repo.insertUserRole(100, testRole1.id.get())
        repo.insertUserRole(100, testRole2.id.get())

        when:
        List<Role> roles = repo.getRolesForUser(100)

        then:
        roles.size() == 2
        roles.stream().anyMatch{role -> role.id.get() == testRole1.id.get()}
        roles.stream().anyMatch{role -> role.id.get() == testRole2.id.get()}

        cleanup:
        repo.deleteUserRolesForUser(100)
    }

    def "retrieving roles for a user does not return roles for other users"() {
        repo.insertUserRole(100, testRole1.id.get())
        repo.insertUserRole(200, testRole2.id.get())

        when:
        List<Role> roles = repo.getRolesForUser(100)

        then:
        roles.size() == 1
        roles.stream().anyMatch{role -> role.id.get() == testRole1.id.get()}

        cleanup:
        repo.deleteUserRolesForUser(100)
        repo.deleteUserRolesForUser(200)
    }

    def "can delete an existing user role"() {
        repo.insertUserRole(100, testRole1.id.get())

        when:
        repo.deleteUserRole(100, testRole1.id.get())

        then:
        repo.getRolesForUser(100).size() == 0
    }

    def "deleting a user role that does not exist does not throw an exception"() {
        when:
        repo.deleteUserRole(100, 200)

        then:
        notThrown(Exception)
    }

    def "can delete all roles for a user"() {
        repo.insertUserRole(100, testRole1.id.get())
        repo.insertUserRole(100, testRole2.id.get())

        when:
        repo.deleteUserRolesForUser(100)

        then:
        repo.getRolesForUser(100).size() == 0
    }

    def "deleting all roles for a user does not affect other users"() {
        repo.insertUserRole(100, testRole1.id.get())
        repo.insertUserRole(200, testRole2.id.get())

        when:
        repo.deleteUserRolesForUser(100)

        then:
        repo.getRolesForUser(100).size() == 0
        repo.getRolesForUser(200).size() == 1

        cleanup:
        repo.deleteUserRolesForUser(100)
        repo.deleteUserRolesForUser(200)
    }

    def "can remove a role from all users"() {
        repo.insertUserRole(100, testRole1.id.get())
        repo.insertUserRole(200, testRole1.id.get())

        when:
        repo.deleteUserRolesForRole(testRole1.id.get())

        then:
        repo.getRolesForUser(100).size() == 0
        repo.getRolesForUser(200).size() == 0
    }

    def "removing a role from all users does not affect other roles"() {
        repo.insertUserRole(100, testRole1.id.get())
        repo.insertUserRole(200, testRole2.id.get())

        when:
        repo.deleteUserRolesForRole(testRole1.id.get())

        then:
        repo.getRolesForUser(100).size() == 0
        repo.getRolesForUser(200).size() == 1

        cleanup:
        repo.deleteUserRolesForUser(200)
    }
}
