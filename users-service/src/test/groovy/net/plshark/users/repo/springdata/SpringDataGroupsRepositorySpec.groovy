package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import net.plshark.testutils.PlsharkFlywayPreparer
import org.junit.Rule
import spock.lang.Specification

class SpringDataGroupsRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SpringDataGroupsRepository repo

    def setup() {
        repo = new SpringDataGroupsRepository(DatabaseClientHelper.buildTestClient(dbRule))
    }

    def "inserting a group returns the inserted group with the ID set"() {
        expect: false
    }

    def "can retrieve a previously inserted group by ID"() {
        expect: false
    }

    def "retrieving a group by ID when no group matches returns empty"() {
        expect: false
    }

    def "can retrieve a previously inserted group by name"() {
        expect: false
    }

    def "retrieving a group by name when no group matches returns empty"() {
        expect: false
    }

    def "can delete a previously inserted group by ID"() {
        expect: false
    }

    def "no exception is thrown when attempting to delete a group that does not exist"() {
        expect: false
    }

    def 'getGroups should return all results when there are less than max results'() {
        expect: false
    }

    def 'getGroups should return up to max results when there are more results'() {
        expect: false
    }

    def 'getGroups should start at the correct offset'() {
        expect: false
    }
}
