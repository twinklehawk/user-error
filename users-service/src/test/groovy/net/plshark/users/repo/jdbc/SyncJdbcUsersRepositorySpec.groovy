package net.plshark.users.repo.jdbc

import com.opentable.db.postgres.embedded.FlywayPreparer
import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import net.plshark.users.model.User
import org.junit.Rule
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Specification

class SyncJdbcUsersRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(FlywayPreparer.forClasspathLocation('db.migration.postgres'))

    SyncJdbcUsersRepository repo

    def setup() {
        repo = new SyncJdbcUsersRepository(new JdbcTemplate(dbRule.testDatabase))
    }

    def "inserting a user returns the inserted user with the ID set"() {
        when:
        User inserted = repo.insert(new User("name", "pass"))

        then:
        inserted.id != null
        inserted.username == "name"
        inserted.password == "pass"

        cleanup:
        repo.delete(inserted.id)
    }

    def "can retrieve a previously inserted user by ID"() {
        User inserted = repo.insert(new User("name", "pass"))

        when:
        User user = repo.getForId(inserted.id).get()

        then:
        user == inserted

        cleanup:
        repo.delete(inserted.id)
    }

    def "can retrieve  previously inserted user by username"() {
        User inserted = repo.insert(new User("name", "pass"))

        when:
        User user = repo.getForUsername("name").get()

        then:
        user == inserted

        cleanup:
        repo.delete(inserted.id)
    }

    def "can delete a previously inserted user by ID"() {
        User inserted = repo.insert(new User("name", "pass"))

        when:
        repo.delete(inserted.id)
        Optional<User> retrieved = repo.getForId(inserted.id)

        then: "should return empty since the row should be gone"
        !retrieved.isPresent()
    }

    def "no exception is thrown when attempting to delete a user that does not exist"() {
        when:
        repo.delete(10000)

        then:
        notThrown(Exception)
    }

    def "update password should change the password if the current password is correct"() {
        User inserted = repo.insert(new User("name", "pass"))

        when:
        repo.updatePassword(inserted.id, "pass", "new-pass")
        User user = repo.getForId(inserted.id).get()

        then:
        user.password == "new-pass"

        cleanup:
        repo.delete(inserted.id)
    }

    def "update password should throw an EmptyResultDataAccessException if the current password is wrong"() {
        User inserted = repo.insert(new User("name", "pass"))

        when:
        repo.updatePassword(inserted.id, "wrong-pass", "new-pass")

        then:
        thrown(EmptyResultDataAccessException)

        when:
        User user = repo.getForId(inserted.id).get()

        then:
        user.password == "pass"

        cleanup:
        repo.delete(inserted.id)
    }

    def "update password should throw an EmptyResultDataAccessException if no user has the ID"() {
        when:
        repo.updatePassword(1000, "pass", "new-pass")

        then:
        thrown(EmptyResultDataAccessException)
    }
}
