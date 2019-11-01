package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.PreparedDbRule
import net.plshark.testutils.PlsharkFlywayPreparer
import net.plshark.users.model.Application
import net.plshark.users.model.Group
import net.plshark.users.model.Role
import org.junit.Rule
import reactor.test.StepVerifier
import spock.lang.Specification

class SpringDataGroupRolesRepositorySpec extends Specification {

    @Rule
    PreparedDbRule dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    SpringDataGroupRolesRepository repo
    SpringDataGroupsRepository groupsRepo
    SpringDataRolesRepository rolesRepo
    SpringDataApplicationsRepository appsRepo

    def setup() {
        def dbClient = DatabaseClientHelper.buildTestClient(dbRule)
        repo = new SpringDataGroupRolesRepository(dbClient)
        groupsRepo = new SpringDataGroupsRepository(dbClient)
        rolesRepo = new SpringDataRolesRepository(dbClient)
        appsRepo = new SpringDataApplicationsRepository(dbClient)
    }

    def 'insert should save a group and role association and should be retrievable'() {
        def app1 = appsRepo.insert(Application.builder().name('app1').build()).block()
        def app2 = appsRepo.insert(Application.builder().name('app2').build()).block()
        def role1 = rolesRepo.insert(Role.builder().name('test1').applicationId(app1.id).build()).block()
        def role2 = rolesRepo.insert(Role.builder().name('test2').applicationId(app2.id).build()).block()
        def group = groupsRepo.insert(Group.create('group1')).block()

        when:
        def roles = repo.insert(group.id, role1.id)
                .then(repo.insert(group.id, role2.id))
                .thenMany(repo.getRolesForGroup(group.id))
                .collectList()
                .block()

        then:
        roles.size() == 2
        roles.contains(role1)
        roles.contains(role2)
    }

    def 'retrieving should return empty when no roles are assigned to the group'() {
        expect:
        StepVerifier.create(repo.getRolesForGroup(100))
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    def 'delete should delete a group/role association'() {
        def app = appsRepo.insert(Application.builder().name('app1').build()).block()
        def role = rolesRepo.insert(Role.builder().name('test1').applicationId(app.id).build()).block()
        def group = groupsRepo.insert(Group.create('group1')).block()

        when:
        def roles = repo.insert(group.id, role.id)
                .then(repo.delete(group.id, role.id))
                .thenMany(repo.getRolesForGroup(group.id))
                .collectList()
                .block()

        then:
        roles.size() == 0
    }

    def 'delete should not throw an exception if the group/role association does not already exist'() {
        expect:
        StepVerifier.create(repo.delete(1, 2))
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    def 'deleting a group ID should delete all associations for that group'() {
        def app1 = appsRepo.insert(Application.builder().name('app1').build()).block()
        def app2 = appsRepo.insert(Application.builder().name('app2').build()).block()
        def role1 = rolesRepo.insert(Role.builder().name('test1').applicationId(app1.id).build()).block()
        def role2 = rolesRepo.insert(Role.builder().name('test2').applicationId(app2.id).build()).block()
        def group = groupsRepo.insert(Group.create('group1')).block()

        when:
        def roles = repo.insert(group.id, role1.id)
                .then(repo.insert(group.id, role2.id))
                .then(repo.deleteForGroup(group.id))
                .thenMany(repo.getRolesForGroup(group.id))
                .collectList()
                .block()

        then:
        roles.size() == 0
    }

    def 'deleting a role ID should delete all associations for that role'() {
        def app = appsRepo.insert(Application.builder().name('app1').build()).block()
        def role = rolesRepo.insert(Role.builder().name('test1').applicationId(app.id).build()).block()
        def group1 = groupsRepo.insert(Group.create('group1')).block()
        def group2 = groupsRepo.insert(Group.create('group2')).block()

        when:
        def roles = repo.insert(group1.id, role.id)
                .then(repo.insert(group2.id, role.id))
                .then(repo.deleteForRole(role.id))
                .thenMany(repo.getRolesForGroup(group1.id).concatWith(repo.getRolesForGroup(group2.id)))
                .collectList()
                .block()

        then:
        roles.size() == 0
    }
}
