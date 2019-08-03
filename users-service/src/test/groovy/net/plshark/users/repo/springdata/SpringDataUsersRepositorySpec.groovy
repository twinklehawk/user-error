package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import net.plshark.testutils.PlsharkFlywayPreparer
import net.plshark.users.model.User
import org.junit.Rule
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier
import spock.lang.Specification

class SpringDataUsersRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SpringDataUsersRepository repo

    def setup() {
        repo = new SpringDataUsersRepository(DatabaseClient.create(
                new PostgresqlConnectionFactory(
                        PostgresqlConnectionConfiguration.builder()
                                .database(dbRule.connectionInfo.dbName)
                                .host('localhost')
                                .port(dbRule.connectionInfo.port)
                                .username(dbRule.connectionInfo.user)
                                .password('')
                                .build())))
    }

    def "inserting a user returns the inserted user with the ID set"() {
        when:
        User inserted = repo.insert(User.create("name", "pass")).block()

        then:
        inserted.id != null
        inserted.username == "name"
        inserted.password == "pass"
    }

    def "can retrieve a previously inserted user by ID"() {
        User inserted = repo.insert(User.create("name", "pass")).block()

        when:
        User user = repo.getForId(inserted.id).block()

        then:
        user == inserted
    }

    def "can retrieve  previously inserted user by username"() {
        User inserted = repo.insert(User.create("name", "pass")).block()

        when:
        User user = repo.getForUsername("name").block()

        then:
        user == inserted
    }

    def "can delete a previously inserted user by ID"() {
        User inserted = repo.insert(User.create("name", "pass")).block()

        when:
        repo.delete(inserted.id).block()
        User retrieved = repo.getForId(inserted.id).block()

        then: "should return empty since the row should be gone"
        retrieved == null
    }

    def "no exception is thrown when attempting to delete a user that does not exist"() {
        when:
        repo.delete(10000).block()

        then:
        notThrown(Exception)
    }

    def "update password should change the password if the current password is correct"() {
        User inserted = repo.insert(User.create("name", "pass")).block()

        when:
        repo.updatePassword(inserted.id, "pass", "new-pass").block()
        User user = repo.getForId(inserted.id).block()

        then:
        user.password == "new-pass"
    }

    def "update password should throw an EmptyResultDataAccessException if the current password is wrong"() {
        User inserted = repo.insert(User.create("name", "pass")).block()

        when:
        StepVerifier.create(repo.updatePassword(inserted.id, "wrong-pass", "new-pass"))
                .expectError(EmptyResultDataAccessException)
                .verify()
        User user = repo.getForId(inserted.id).block()

        then:
        user.password == "pass"
    }

    def "update password should throw an EmptyResultDataAccessException if no user has the ID"() {
        expect:
        StepVerifier.create(repo.updatePassword(1000, "pass", "new-pass"))
                .expectError(EmptyResultDataAccessException)
                .verify()
    }

    def 'getAll should return all results when there are less than max results'() {
        repo.insert(User.create("name", "pass")).block()
        repo.insert(User.create("name2", "pass")).block()

        when:
        List<User> users = repo.getAll(5, 0).collectList().block()

        then:
        users.size() == 3
        // admin is inserted by the migration scripts
        users.get(0).username == 'admin'
        users.get(1).username == 'name'
        users.get(2).username == 'name2'
    }

    def 'getAll should return up to max results when there are more results'() {
        repo.insert(User.create("name", "pass")).block()
        repo.insert(User.create("name2", "pass")).block()
        repo.insert(User.create("name3", "pass")).block()

        when:
        List<User> users = repo.getAll(2, 0).collectList().block()

        then:
        users.size() == 2
        users.get(0).username == 'admin'
        users.get(1).username == 'name'
    }

    def 'getAll should start at the correct offset'() {
        repo.insert(User.create("name", "pass")).block()
        repo.insert(User.create("name2", "pass")).block()
        repo.insert(User.create("name3", "pass")).block()

        when:
        List<User> users = repo.getAll(2, 2).collectList().block()

        then:
        users.size() == 2
        users.get(0).username == 'name2'
        users.get(1).username == 'name3'
    }
}
