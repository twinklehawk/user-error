package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import net.plshark.testutils.PlsharkFlywayPreparer

import org.junit.Rule
import reactor.test.StepVerifier
import spock.lang.Specification

class SpringDataGroupsRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SpringDataGroupsRepository repo

    def setup() {
        repo = new SpringDataGroupsRepository(DatabaseClientHelper.buildTestClient(dbRule))
    }

    def "inserting a group returns the inserted group with the ID set"() {
        when:
        def group = repo.insert(Group.builder().name('test-group').build()).block()

        then:
        group.id != null
        group.name == 'test-group'
    }

    def "can retrieve a previously inserted group by ID"() {
        when:
        def group = repo.insert(Group.builder().name('group').build()).block()

        then:
        StepVerifier.create(repo.getForId(group.id))
                .expectNext(group)
                .verifyComplete()
    }

    def "retrieving a group by ID when no group matches returns empty"() {
        expect:
        StepVerifier.create(repo.getForId(100))
                .expectNextCount(0)
                .verifyComplete()
    }

    def "can retrieve a previously inserted group by name"() {
        when:
        def group = repo.insert(Group.builder().name('group').build()).block()

        then:
        StepVerifier.create(repo.getForName('group'))
                .expectNext(group)
                .verifyComplete()
    }

    def "retrieving a group by name when no group matches returns empty"() {
        expect:
        StepVerifier.create(repo.getForName('name'))
                .expectNextCount(0)
                .verifyComplete()
    }

    def "can delete a previously inserted group by ID"() {
        when:
        def group = repo.insert(Group.builder().name('group').build()).block()
        repo.delete(group.id).block()

        then:
        StepVerifier.create(repo.getForName('name'))
                .expectNextCount(0)
                .verifyComplete()
    }

    def "no exception is thrown when attempting to delete a group that does not exist"() {
        expect:
        StepVerifier.create(repo.delete(200))
                .verifyComplete()
    }

    def 'getGroups should return all results when there are less than max results'() {
        when:
        repo.insert(Group.builder().name('group1').build())
                .then(repo.insert(Group.builder().name('group2').build()))
                .then(repo.insert(Group.builder().name('group3').build()))
                .block()

        then:
        StepVerifier.create(repo.getGroups(50, 0))
                // one group is inserted by the migration scripts
                .expectNextCount(4)
                .verifyComplete()
    }

    def 'getGroups should return up to max results when there are more results'() {
        when:
        repo.insert(Group.builder().name('group1').build())
                .then(repo.insert(Group.builder().name('group2').build()))
                .then(repo.insert(Group.builder().name('group3').build()))
                .block()

        then:
        StepVerifier.create(repo.getGroups(2, 0))
                .expectNextCount(2)
                .verifyComplete()
    }

    def 'getGroups should start at the correct offset'() {
        when:
        repo.insert(Group.builder().name('group1').build())
                .then(repo.insert(Group.builder().name('group2').build()))
                .then(repo.insert(Group.builder().name('group3').build()))
                .block()

        then:
        StepVerifier.create(repo.getGroups(2, 2))
                .expectNextMatches({ group -> group.name == 'group2' })
                .expectNextMatches({ group -> group.name == 'group3' })
                .verifyComplete()
    }
}
