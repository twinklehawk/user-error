package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import net.plshark.testutils.PlsharkFlywayPreparer
import net.plshark.users.model.Application
import net.plshark.users.model.Group
import net.plshark.users.model.Role
import net.plshark.users.model.User
import org.junit.Rule
import reactor.test.StepVerifier
import spock.lang.Specification

class SpringDataUserGroupsRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SpringDataUserGroupsRepository repo
    SpringDataUsersRepository usersRepo
    SpringDataGroupsRepository groupsRepo
    SpringDataApplicationsRepository appsRepo
    SpringDataRolesRepository rolesRepo
    SpringDataGroupRolesRepository groupRolesRepo

    def setup() {
        def dbClient = DatabaseClientHelper.buildTestClient(dbRule)
        repo = new SpringDataUserGroupsRepository(dbClient)
        groupsRepo = new SpringDataGroupsRepository(dbClient)
        usersRepo = new SpringDataUsersRepository(dbClient)
        appsRepo = new SpringDataApplicationsRepository(dbClient)
        rolesRepo = new SpringDataRolesRepository(dbClient)
        groupRolesRepo = new SpringDataGroupRolesRepository(dbClient)
    }

    def 'insert should save a group and user association and should be retrievable'() {
        def group = groupsRepo.insert(Group.builder().name('test-group').build()).block()
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
        def group = groupsRepo.insert(Group.builder().name('test-group').build()).block()
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
        def group1 = groupsRepo.insert(Group.builder().name('group1').build()).block()
        def group2 = groupsRepo.insert(Group.builder().name('group2').build()).block()
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
        def group1 = groupsRepo.insert(Group.builder().name('group1').build()).block()
        def group2 = groupsRepo.insert(Group.builder().name('group2').build()).block()
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

    def 'retrieving roles should return each role in each group the user belongs to'() {
        def app1 = appsRepo.insert(Application.builder().name('test-app').build()).block()
        def role1 = rolesRepo.insert(Role.builder().applicationId(app1.id).name('role1').build()).block()
        def role2 = rolesRepo.insert(Role.builder().applicationId(app1.id).name('role2').build()).block()
        rolesRepo.insert(Role.builder().applicationId(app1.id).name('role3').build()).block()
        def group = groupsRepo.insert(Group.builder().name('test-group').build()).block()
        def user = usersRepo.insert(User.builder().username('user').password('pass').build()).block()
        groupRolesRepo.insert(group.id, role1.id)
                .then(groupRolesRepo.insert(group.id, role2.id))
                .then(repo.insert(user.id, group.id)).block()

        expect:
        StepVerifier.create(repo.getGroupRolesForUser(user.id).collectList())
                .expectNextMatches({ list -> list.size() == 2 && list.contains(role1) && list.contains(role2) })
                .verifyComplete()
    }
}
