package net.plshark.users.repo.jdbc

import com.opentable.db.postgres.embedded.FlywayPreparer
import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import net.plshark.users.model.Role
import org.junit.Rule
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Specification

class SyncJdbcRolesRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(FlywayPreparer.forClasspathLocation('db/migration/postgres'))

    SyncJdbcRolesRepository repo

    def setup() {
        repo = new SyncJdbcRolesRepository(new JdbcTemplate(dbRule.testDatabase))
    }

    def "inserting a role returns the inserted role with the ID set"() {
        when:
        Role inserted = repo.insert(new Role("test-role"))

        then:
        inserted.id != null
        inserted.name == "test-role"

        cleanup:
        if (inserted != null)
            repo.delete(inserted.id)
    }

    def "can retrieve a previously inserted role by ID"() {
        Role inserted = repo.insert(new Role("test-role"))

        when:
        Role role = repo.getForId(inserted.id).get()

        then:
        role == inserted

        cleanup:
        repo.delete(inserted.id)
    }

    def "retrieving a role by ID when no role matches returns an empty optional"() {
        expect:
        !repo.getForId(1000).isPresent()
    }

    def "can retrieve a previously inserted role by name"() {
        Role inserted = repo.insert(new Role("test-role"))

        when:
        Role role = repo.getForName("test-role").get()

        then:
        role == inserted

        cleanup:
        repo.delete(inserted.id)
    }

    def "retrieving a role by name when no role matches throws EmptyResultDataAccessException"() {
        when:
        Optional<Role> role = repo.getForName("test-role")

        then:
        !role.isPresent()
    }

    def "can delete a previously inserted role by ID"() {
        Role inserted = repo.insert(new Role("test-role"))

        when:
        repo.delete(inserted.id)
        Optional<Role> retrieved = repo.getForId(inserted.id)

        then: "get should return empty since the row should be gone"
        !retrieved.isPresent()
    }

    def "no exception is thrown when attempting to delete a role that does not exist"() {
        when:
        repo.delete(10000)

        then:
        notThrown(Exception)
    }
}
