package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.plshark.testutils.DbIntTest
import net.plshark.users.model.ApplicationCreate
import net.plshark.users.model.GroupCreate
import net.plshark.users.model.RoleCreate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.r2dbc.core.DatabaseClient

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
    fun `insert should save a group and role association and should be retrievable`() = runBlocking {
        val app1 = appsRepo.insert(ApplicationCreate("app1"))
        val app2 = appsRepo.insert(ApplicationCreate("app2"))
        val role1 = rolesRepo.insert(RoleCreate(app1.id, "test1"))
        val role2 = rolesRepo.insert(RoleCreate(app2.id, "test2"))
        val group = groupsRepo.insert(GroupCreate("group1"))

        repo.insert(group.id, role1.id)
        repo.insert(group.id, role2.id)
        val roles = repo.findRolesForGroup(group.id).toList()

        assertEquals(2, roles.size)
        assertTrue(roles.contains(role1))
        assertTrue(roles.contains(role2))
    }

    @Test
    fun `retrieving should return empty when no roles are assigned to the group`() = runBlocking {
        assertEquals(0, repo.findRolesForGroup(100).count())
    }

    @Test
    fun `delete should delete a group-role association`() = runBlocking {
        val app = appsRepo.insert(ApplicationCreate("app1"))
        val role = rolesRepo.insert(RoleCreate(app.id, "test1"))
        val group = groupsRepo.insert(GroupCreate("group1"))

        repo.insert(group.id, role.id)
        repo.deleteById(group.id, role.id)
        val roles = repo.findRolesForGroup(group.id).toList()

        assertTrue(roles.isEmpty())
    }

    @Test
    fun `delete should not throw an exception if the group-role association does not already exist`() = runBlocking {
        repo.deleteById(1, 2)
    }

    @Test
    fun `deleting a group ID should delete all associations for that group`() = runBlocking {
        val app1 = appsRepo.insert(ApplicationCreate("app1"))
        val app2 = appsRepo.insert(ApplicationCreate("app2"))
        val role1 = rolesRepo.insert(RoleCreate(app1.id, "test1"))
        val role2 = rolesRepo.insert(RoleCreate(app2.id, "test2"))
        val group = groupsRepo.insert(GroupCreate("group1"))

        repo.insert(group.id, role1.id)
        repo.insert(group.id, role2.id)
        repo.deleteByGroupId(group.id)
        val roles = repo.findRolesForGroup(group.id).toList()

        assertTrue(roles.isEmpty())
    }

    @Test
    fun `deleting a role ID should delete all associations for that role`() = runBlocking {
        val app = appsRepo.insert(ApplicationCreate("app1"))
        val role = rolesRepo.insert(RoleCreate(app.id, "test1"))
        val group1 = groupsRepo.insert(GroupCreate("group1"))
        val group2 = groupsRepo.insert(GroupCreate("group2"))

        repo.insert(group1.id, role.id)
        repo.insert(group2.id, role.id)
        repo.deleteByRoleId(role.id)
        assertTrue(repo.findRolesForGroup(group1.id).toList().isEmpty())
        assertTrue(repo.findRolesForGroup(group2.id).toList().isEmpty())
    }
}
