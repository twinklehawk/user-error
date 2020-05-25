package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import net.plshark.testutils.DbIntTest
import net.plshark.users.model.ApplicationCreate
import net.plshark.users.model.GroupCreate
import net.plshark.users.model.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier

class SpringDataGroupRolesRepositoryTest : DbIntTest() {

    private lateinit var repo: SpringDataGroupRolesRepository
    private lateinit var groupsRepo: SpringDataGroupsRepository
    private lateinit var rolesRepo: SpringDataRolesRepository
    private lateinit var appsRepo: SpringDataApplicationsRepository

    @BeforeEach
    fun setup() {
        val connectionFactory = ConnectionFactories.get(DB_URL)
        val dbClient = DatabaseClient.create(connectionFactory)
        repo = SpringDataGroupRolesRepository(dbClient)
        groupsRepo = SpringDataGroupsRepository(dbClient)
        rolesRepo = SpringDataRolesRepository(dbClient)
        appsRepo = SpringDataApplicationsRepository(dbClient)
    }

    @Test
    fun `insert should save a group and role association and should be retrievable`() {
        val app1 = appsRepo.insert(ApplicationCreate("app1")).block()!!
        val app2 = appsRepo.insert(ApplicationCreate("app2")).block()!!
        val role1 = rolesRepo.insert(Role(null, app1.id, "test1")).block()!!
        val role2 = rolesRepo.insert(Role(null, app2.id, "test2")).block()!!
        val group = groupsRepo.insert(GroupCreate("group1")).block()!!

        val roles = repo.insert(group.id, role1.id!!)
                .then(repo.insert(group.id, role2.id!!))
                .thenMany(repo.getRolesForGroup(group.id))
                .collectList()
                .block()!!

        assertEquals(2, roles.size)
        assertTrue(roles.contains(role1))
        assertTrue(roles.contains(role2))
    }

    @Test
    fun `retrieving should return empty when no roles are assigned to the group`() {
        StepVerifier.create(repo.getRolesForGroup(100))
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    @Test
    fun `delete should delete a group-role association`() {
        val app = appsRepo.insert(ApplicationCreate("app1")).block()!!
        val role = rolesRepo.insert(Role(null, app.id, "test1")).block()!!
        val group = groupsRepo.insert(GroupCreate("group1")).block()!!

        val roles = repo.insert(group.id, role.id!!)
                .then(repo.delete(group.id, role.id!!))
                .thenMany(repo.getRolesForGroup(group.id))
                .collectList()
                .block()!!

        assertTrue(roles.isEmpty())
    }

    @Test
    fun `delete should not throw an exception if the group-role association does not already exist`() {
        StepVerifier.create(repo.delete(1, 2))
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    @Test
    fun `deleting a group ID should delete all associations for that group`() {
        val app1 = appsRepo.insert(ApplicationCreate("app1")).block()!!
        val app2 = appsRepo.insert(ApplicationCreate("app2")).block()!!
        val role1 = rolesRepo.insert(Role(null, app1.id, "test1")).block()!!
        val role2 = rolesRepo.insert(Role(null, app2.id, "test2")).block()!!
        val group = groupsRepo.insert(GroupCreate("group1")).block()!!

        val roles = repo.insert(group.id, role1.id!!)
                .then(repo.insert(group.id, role2.id!!))
                .then(repo.deleteForGroup(group.id))
                .thenMany(repo.getRolesForGroup(group.id))
                .collectList()
                .block()!!

        assertTrue(roles.isEmpty())
    }

    @Test
    fun `deleting a role ID should delete all associations for that role`() {
        val app = appsRepo.insert(ApplicationCreate("app1")).block()!!
        val role = rolesRepo.insert(Role(null, app.id, "test1")).block()!!
        val group1 = groupsRepo.insert(GroupCreate("group1")).block()!!
        val group2 = groupsRepo.insert(GroupCreate("group2")).block()!!

        val roles = repo.insert(group1.id, role.id!!)
                .then(repo.insert(group2.id, role.id!!))
                .then(repo.deleteForRole(role.id!!))
                .thenMany(repo.getRolesForGroup(group1.id).concatWith(repo.getRolesForGroup(group2.id)))
                .collectList()
                .block()!!

        assertTrue(roles.isEmpty())
    }
}
