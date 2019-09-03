package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import net.plshark.testutils.PlsharkFlywayPreparer
import org.junit.Rule
import spock.lang.Specification

class SpringDataUserGroupsRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SpringDataUserGroupsRepository repo;
    SpringDataUsersRepository usersRepo;
    SpringDataGroupsRepository groupsRepo;

    def setup() {
        def dbClient = DatabaseClientHelper.buildTestClient(dbRule)
        repo = new SpringDataUserGroupsRepository(dbClient)
        groupsRepo = new SpringDataGroupsRepository(dbClient)
        usersRepo = new SpringDataUsersRepository(dbClient)
    }

    def 'insert should save a group and user association and should be retrievable'() {
        expect:
        false
    }

    def 'retrieving should return empty when no users are assigned to the group'() {
        expect:
        false
    }

    def 'delete should delete a group/user association'() {
        expect:
        false
    }

    def 'delete should not throw an exception if the group/user association does not already exist'() {
        expect:
        false
    }

    def 'deleting a group ID should delete all associations for that group'() {
        expect:
        false
    }

    def 'deleting a user ID should delete all associations for that user'() {
        expect:
        false
    }
}
