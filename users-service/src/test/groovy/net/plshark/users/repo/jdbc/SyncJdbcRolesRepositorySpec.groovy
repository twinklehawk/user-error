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

    def 'getRoles should return all results when there are less than max results'() {
        repo.insert(new Role("name"))
        repo.insert(new Role("name2"))

        when:
        List<Role> roles = repo.getRoles(5, 0)

        then:
        roles.size() == 4
        // these are inserted by the migration scripts
        roles.get(0).name == 'notes-user'
        roles.get(1).name == 'notes-admin'
        roles.get(2).name == 'name'
        roles.get(3).name == 'name2'
    }

    def 'getRoles should return up to max results when there are more results'() {
        repo.insert(new Role("name"))
        repo.insert(new Role("name2"))
        repo.insert(new Role("name3"))

        when:
        List<Role> roles = repo.getRoles(2, 0)

        then:
        roles.size() == 2
        roles.get(0).name == 'notes-user'
        roles.get(1).name == 'notes-admin'
    }

    def 'getRoles should start at the correct offset'() {
        repo.insert(new Role("name"))
        repo.insert(new Role("name2"))
        repo.insert(new Role("name3"))

        when:
        List<Role> roles = repo.getRoles(2, 2)

        then:
        roles.size() == 2
        roles.get(0).name == 'name'
        roles.get(1).name == 'name2'
    }
}
