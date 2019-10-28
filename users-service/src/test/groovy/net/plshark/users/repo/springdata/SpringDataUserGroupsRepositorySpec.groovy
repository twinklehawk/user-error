package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import net.plshark.testutils.PlsharkFlywayPreparer
import net.plshark.users.model.Group
import net.plshark.users.model.User
import org.junit.Rule
import spock.lang.Specification

class SpringDataUserGroupsRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SpringDataUserGroupsRepository repo
    SpringDataUsersRepository usersRepo
    SpringDataGroupsRepository groupsRepo

    def setup() {
        def dbClient = DatabaseClientHelper.buildTestClient(dbRule)
        repo = new SpringDataUserGroupsRepository(dbClient)
        groupsRepo = new SpringDataGroupsRepository(dbClient)
        usersRepo = new SpringDataUsersRepository(dbClient)
    }

    def 'insert should save a group and user association and should be retrievable'() {
        def group = groupsRepo.insert(Group.create('test-group')).block()
        def user = usersRepo.insert(User.builder().username('test-user').password('pass').build()).block()

        when:
        repo.insert(user.id, group.id).block()

        then:
        repo.getGroupsForUser(user.id).collectList().block() == Collections.singletonList(group)
    }

    def 'retrieving should return empty when no users are assigned to the group'() {
        expect:
        repo.getGroupsForUser(123).count().block() == 0
    }

    def 'delete should delete a group/user association'() {
        def group = groupsRepo.insert(Group.create('test-group')).block()
        def user = usersRepo.insert(User.builder().username('test-user').password('pass').build()).block()
        repo.insert(user.id, group.id).block()

        when:
        repo.delete(user.id, group.id).block()

        then:
        repo.getGroupsForUser(user.id).count().block() == 0
    }

    def 'delete should not throw an exception if the group/user association does not already exist'() {
        when:
        repo.delete(100, 200).block()

        then:
        noExceptionThrown()
    }

    def 'deleting a group ID should delete all associations for that group'() {
        def group1 = groupsRepo.insert(Group.create('group1')).block()
        def group2 = groupsRepo.insert(Group.create('group2')).block()
        def user1 = usersRepo.insert(User.builder().username('user1').password('pass').build()).block()
        def user2 = usersRepo.insert(User.builder().username('user2').password('pass').build()).block()
        def user3 = usersRepo.insert(User.builder().username('user3').password('pass').build()).block()
        repo.insert(user1.id, group1.id)
            .then(repo.insert(user2.id, group1.id))
            .then(repo.insert(user2.id, group2.id))
            .then(repo.insert(user3.id, group2.id))
            .block()

        when:
        repo.deleteUserGroupsForGroup(group1.id).block()

        then:
        repo.getGroupsForUser(user1.id).count().block() == 0
        repo.getGroupsForUser(user2.id).collectList().block() == Collections.singletonList(group2)
        repo.getGroupsForUser(user3.id).collectList().block() == Collections.singletonList(group2)
    }

    def 'deleting a user ID should delete all associations for that user'() {
        def group1 = groupsRepo.insert(Group.create('group1')).block()
        def group2 = groupsRepo.insert(Group.create('group2')).block()
        def user1 = usersRepo.insert(User.builder().username('user1').password('pass').build()).block()
        def user2 = usersRepo.insert(User.builder().username('user2').password('pass').build()).block()
        repo.insert(user1.id, group1.id)
                .then(repo.insert(user1.id, group2.id))
                .then(repo.insert(user2.id, group1.id))
                .then(repo.insert(user2.id, group2.id))
                .block()

        when:
        repo.deleteUserGroupsForUser(user1.id).block()

        then:
        repo.getGroupsForUser(user1.id).count().block() == 0
        repo.getGroupsForUser(user2.id).collectList().block() == Arrays.asList(group1, group2)
    }
}
