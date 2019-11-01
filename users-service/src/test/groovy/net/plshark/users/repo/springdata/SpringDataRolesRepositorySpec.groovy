package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import net.plshark.testutils.PlsharkFlywayPreparer
import net.plshark.users.model.Application
import net.plshark.users.model.Role
import org.junit.Rule
import reactor.test.StepVerifier
import spock.lang.Specification

class SpringDataRolesRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SpringDataRolesRepository repo
    SpringDataApplicationsRepository appsRepo

    def setup() {
        def db = DatabaseClientHelper.buildTestClient(dbRule)
        repo = new SpringDataRolesRepository(db)
        appsRepo = new SpringDataApplicationsRepository(db)
    }

    def "inserting a role returns the inserted role with the ID set"() {
        def app = appsRepo.insert(Application.builder().name('app').build()).block()

        when:
        Role inserted = repo.insert(Role.builder().name('test-role').applicationId(app.id).build()).block()

        then:
        inserted.id != null
        inserted.name == "test-role"
        inserted.applicationId == app.id
    }

    def "can retrieve a previously inserted role by ID"() {
        def app = appsRepo.insert(Application.builder().name('app').build()).block()
        Role inserted = repo.insert(Role.builder().name('test-role').applicationId(app.id).build()).block()

        when:
        Role role = repo.get(inserted.id).block()

        then:
        role == inserted
    }

    def "retrieving a role by ID when no role matches returns empty"() {
        expect:
        StepVerifier.create(repo.get(1000))
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    def "can retrieve a previously inserted role by name"() {
        def app = appsRepo.insert(Application.builder().name('app').build()).block()
        Role inserted = repo.insert(Role.builder().name('test-role').applicationId(app.id).build()).block()

        when:
        Role role = repo.get(app.id, "test-role").block()

        then:
        role == inserted
    }

    def "retrieving a role by name when no role matches returns empty"() {
        expect:
        StepVerifier.create(repo.get(1, "test-role"))
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    def "can delete a previously inserted role by ID"() {
        def app = appsRepo.insert(Application.builder().name('app').build()).block()
        Role inserted = repo.insert(Role.builder().name('test-role').applicationId(app.id).build()).block()

        when:
        repo.delete(inserted.id).block()
        Role retrieved = repo.get(inserted.id).block()

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
        def app = appsRepo.insert(Application.builder().name('app').build()).block()
        repo.insert(Role.builder().name('name').applicationId(app.id).build())
                .then(repo.insert(Role.builder().name('name2').applicationId(app.id).build())).block()

        when:
        List<Role> roles = repo.getRoles(5, 0).collectList().block()

        then:
        roles.size() == 4
        // these are inserted by the migration scripts
        roles.get(0).name == 'bad-users-user'
        roles.get(1).name == 'bad-users-admin'
        roles.get(2).name == 'name'
        roles.get(3).name == 'name2'
    }

    def 'getRoles should return up to max results when there are more results'() {
        def app = appsRepo.insert(Application.builder().name('app').build()).block()
        repo.insert(Role.builder().name('name').applicationId(app.id).build()).block()
        repo.insert(Role.builder().name('name2').applicationId(app.id).build()).block()
        repo.insert(Role.builder().name('name3').applicationId(app.id).build()).block()

        when:
        List<Role> roles = repo.getRoles(2, 0).collectList().block()

        then:
        roles.size() == 2
        roles.get(0).name == 'bad-users-user'
        roles.get(1).name == 'bad-users-admin'
    }

    def 'getRoles should start at the correct offset'() {
        def app = appsRepo.insert(Application.builder().name('app').build()).block()
        repo.insert(Role.builder().name('name').applicationId(app.id).build())
                .then(repo.insert(Role.builder().name('name2').applicationId(app.id).build()))
                .then(repo.insert(Role.builder().name('name3').applicationId(app.id).build())).block()

        when:
        List<Role> roles = repo.getRoles(2, 2).collectList().block()

        then:
        roles.size() == 2
        roles.get(0).name == 'name'
        roles.get(1).name == 'name2'
    }

    def 'getRolesForApplication should return all rows with a matching application ID'() {
        def app = appsRepo.insert(Application.builder().name('app').build()).block()
        def app2 = appsRepo.insert(Application.builder().name('app2').build()).block()
        repo.insert(Role.builder().name('r1').applicationId(app.id).build()).block()
        repo.insert(Role.builder().name('r2').applicationId(app.id).build()).block()
        repo.insert(Role.builder().name('r3').applicationId(app2.id).build()).block()

        expect:
        StepVerifier.create(repo.getRolesForApplication(app.id))
                .expectNextMatches({ r -> r.name == 'r1' })
                .expectNextMatches({ r -> r.name == 'r2' })
                .verifyComplete()
    }
}
