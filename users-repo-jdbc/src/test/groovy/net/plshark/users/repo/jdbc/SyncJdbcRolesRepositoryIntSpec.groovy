package net.plshark.users.repo.jdbc

import javax.inject.Inject

import org.springframework.boot.test.context.SpringBootTest

import net.plshark.jdbc.RepoTestConfig
import net.plshark.users.Role
import spock.lang.Specification

@SpringBootTest(classes = RepoTestConfig.class)
class SyncJdbcRolesRepositoryIntSpec extends Specification {

    @Inject
    SyncJdbcRolesRepository repo

    def "inserting a role returns the inserted role with the ID set"() {
        when:
        Role inserted = repo.insert(new Role("test-role"))

        then:
        inserted.id.isPresent()
        inserted.name == "test-role"

        cleanup:
        repo.delete(inserted.id.get())
    }

    def "can retrieve a previously inserted role by ID"() {
        Role inserted = repo.insert(new Role("test-role"))

        when:
        Role role = repo.getForId(inserted.id.get()).get()

        then:
        role == inserted

        cleanup:
        repo.delete(inserted.id.get())
    }

    def "retrieving a role by ID when no role matches returns an empty optional"() {
        expect:
        repo.getForId(1000).isPresent() == false
    }

    def "can retrieve a previously inserted role by name"() {
        Role inserted = repo.insert(new Role("test-role"))

        when:
        Role role = repo.getForName("test-role").get()

        then:
        role == inserted

        cleanup:
        repo.delete(inserted.id.get())
    }

    def "retrieving a role by name when no role matches throws EmptyResultDataAccessException"() {
        when:
        Optional<Role> role = repo.getForName("test-role")

        then:
        role.isPresent() == false
    }

    def "can delete a previously inserted role by ID"() {
        Role inserted = repo.insert(new Role("test-role"))

        when:
        repo.delete(inserted.id.get())
        Optional<Role> retrieved = repo.getForId(inserted.id.get())

        then: "get should return empty since the row should be gone"
        retrieved.isPresent() == false
    }

    def "no exception is thrown when attempting to delete a role that does not exist"() {
        when:
        repo.delete(10000)

        then:
        notThrown(Exception)
    }
}
