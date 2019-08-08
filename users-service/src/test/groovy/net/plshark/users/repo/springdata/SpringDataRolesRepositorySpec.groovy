package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import net.plshark.testutils.PlsharkFlywayPreparer
import net.plshark.users.model.Role
import org.junit.Rule
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier
import spock.lang.Specification

class SpringDataRolesRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SpringDataRolesRepository repo

    def setup() {
        repo = new SpringDataRolesRepository(DatabaseClient.create(
                new PostgresqlConnectionFactory(
                        PostgresqlConnectionConfiguration.builder()
                                .database(dbRule.connectionInfo.dbName)
                                .host('localhost')
                                .port(dbRule.connectionInfo.port)
                                .username(dbRule.connectionInfo.user)
                                .password('')
                                .build())))
    }

    def "inserting a role returns the inserted role with the ID set"() {
        when:
        Role inserted = repo.insert(Role.create("test-role", "app")).block()

        then:
        inserted.id != null
        inserted.name == "test-role"
        inserted.application == "app"
    }

    def "can retrieve a previously inserted role by ID"() {
        Role inserted = repo.insert(Role.create("test-role", "app")).block()

        when:
        Role role = repo.getForId(inserted.id).block()

        then:
        role == inserted
    }

    def "retrieving a role by ID when no role matches returns an empty optional"() {
        expect:
        StepVerifier.create(repo.getForId(1000))
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    def "can retrieve a previously inserted role by name"() {
        Role inserted = repo.insert(Role.create("test-role", "test-app")).block()

        when:
        Role role = repo.getForName("test-role", "test-app").block()

        then:
        role == inserted
    }

    def "retrieving a role by name when no role matches return an empty optional"() {
        expect:
        StepVerifier.create(repo.getForName("test-role", "test-app"))
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    def "can delete a previously inserted role by ID"() {
        Role inserted = repo.insert(Role.create("test-role", "application")).block()

        when:
        repo.delete(inserted.id).block()
        Role retrieved = repo.getForId(inserted.id).block()

        then: "get should return empty since the row should be gone"
        retrieved == null
    }

    def "no exception is thrown when attempting to delete a role that does not exist"() {
        when:
        repo.delete(10000).block()

        then:
        notThrown(Exception)
    }

    def 'getRoles should return all results when there are less than max results'() {
        repo.insert(Role.create("name", "app"))
                .then(repo.insert(Role.create("name2", "app"))).block()

        when:
        List<Role> roles = repo.getRoles(5, 0).collectList().block()

        then:
        roles.size() == 4
        // these are inserted by the migration scripts
        roles.get(0).name == 'users-user'
        roles.get(1).name == 'users-admin'
        roles.get(2).name == 'name'
        roles.get(3).name == 'name2'
    }

    def 'getRoles should return up to max results when there are more results'() {
        repo.insert(Role.create("name", "app")).block()
        repo.insert(Role.create("name2", "app")).block()
        repo.insert(Role.create("name3", "app")).block()

        when:
        List<Role> roles = repo.getRoles(2, 0).collectList().block()

        then:
        roles.size() == 2
        roles.get(0).name == 'users-user'
        roles.get(1).name == 'users-admin'
    }

    def 'getRoles should start at the correct offset'() {
        repo.insert(Role.create("name", "app"))
                .then(repo.insert(Role.create("name2", "app")))
                .then(repo.insert(Role.create("name3", "app"))).block()

        when:
        List<Role> roles = repo.getRoles(2, 2).collectList().block()

        then:
        roles.size() == 2
        roles.get(0).name == 'name'
        roles.get(1).name == 'name2'
    }
}
