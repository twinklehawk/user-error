package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import net.plshark.testutils.PlsharkFlywayPreparer

import org.junit.Rule
import reactor.test.StepVerifier
import spock.lang.Specification

class SpringDataApplicationsRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SpringDataApplicationsRepository repo

    def setup() {
        def db = DatabaseClientHelper.buildTestClient(dbRule)
        repo = new SpringDataApplicationsRepository(db)
    }

    def 'inserting an application returns the inserted application with the ID set'() {
        when:
        def inserted = repo.insert(Application.builder().name('app').build()).block()

        then:
        inserted.id != null
        inserted.name == 'app'
    }

    def 'can retrieve a previously inserted application by ID'() {
        def inserted = repo.insert(Application.builder().name('test-app').build()).block()

        when:
        def app = repo.get(inserted.id).block()

        then:
        app == inserted
    }

    def 'retrieving an application by ID when no application matches returns empty'() {
        expect:
        StepVerifier.create(repo.get(1000))
                .verifyComplete()
    }

    def 'can retrieve a previously inserted application by name'() {
        def inserted = repo.insert(Application.builder().name('test-app').build()).block()

        when:
        def app = repo.get(inserted.name).block()

        then:
        app == inserted
    }

    def 'retrieving an application by name when no application matches returns empty'() {
        expect:
        StepVerifier.create(repo.get('app'))
                .verifyComplete()
    }

    def 'can delete a previously inserted application by ID'() {
        def inserted = repo.insert(Application.builder().name('test-app').build()).block()

        when:
        repo.delete(inserted.id).block()
        def retrieved = repo.get(inserted.id).block()

        then:
        retrieved == null
    }

    def 'no exception is thrown when attempting to delete an application by ID that does not exist'() {
        when:
        repo.delete(10000).block()

        then:
        notThrown(Exception)
    }

    def 'can delete a previously inserted application by name'() {
        def inserted = repo.insert(Application.builder().name('test-app').build()).block()

        when:
        repo.delete(inserted.name).block()
        def retrieved = repo.get(inserted.id).block()

        then:
        retrieved == null
    }

    def 'no exception is thrown when attempting to delete an application by name that does not exist'() {
        when:
        repo.delete('test').block()

        then:
        notThrown(Exception)
    }
}
