package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import kotlinx.coroutines.runBlocking
import net.plshark.testutils.DbIntTest
import net.plshark.users.model.ApplicationCreate
import net.plshark.users.model.GroupCreate
import net.plshark.users.model.RoleCreate
import net.plshark.users.model.UserCreate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier

class SpringDataUserGroupsRepositoryTest : DbIntTest() {

    private lateinit var repo: SpringDataUserGroupsRepository
    private lateinit var usersRepo: SpringDataUsersRepository
    private lateinit var groupsRepo: SpringDataGroupsRepository
    private lateinit var appsRepo: SpringDataApplicationsRepository
    private lateinit var rolesRepo: SpringDataRolesRepository
    private lateinit var groupRolesRepo: SpringDataGroupRolesRepository

    @BeforeEach
    fun setup() {
        val connectionFactory = ConnectionFactories.get(DB_URL)
        val dbClient = DatabaseClient.create(connectionFactory)
        repo = SpringDataUserGroupsRepository(dbClient)
        groupsRepo = SpringDataGroupsRepository(dbClient)
        usersRepo = SpringDataUsersRepository(dbClient)
        appsRepo = SpringDataApplicationsRepository(dbClient)
        rolesRepo = SpringDataRolesRepository(dbClient)
        groupRolesRepo = SpringDataGroupRolesRepository(dbClient)
    }

    @Test
    fun `insert should save a group and user association and should be retrievable`() {
        val group = groupsRepo.insert(GroupCreate("test-name")).block()!!
        val user = usersRepo.insert(UserCreate("test-user", "pass")).block()!!

        repo.insert(user.id, group.id).block()

        assertEquals(listOf(group), repo.findGroupsByUserId(user.id).collectList().block())
    }

    @Test
    fun `retrieving should return empty when no users are assigned to the group`() {
        assertEquals(0, repo.findGroupsByUserId(123).count().block())
    }

    @Test
    fun `delete should delete a group-user association`() {
        val group = groupsRepo.insert(GroupCreate("test-group")).block()!!
        val user = usersRepo.insert(UserCreate("test-user", "pass")).block()!!
        repo.insert(user.id, group.id).block()

        repo.deleteById(user.id, group.id).block()

        assertEquals(0, repo.findGroupsByUserId(user.id).count().block())
    }

    @Test
    fun `delete should not throw an exception if the group-user association does not already exist`() {
        repo.deleteById(100, 200).block()
    }

    @Test
    fun `retrieving roles should return each role in each group the user belongs to`(): Unit = runBlocking {
        val app1 = appsRepo.insert(ApplicationCreate("test-app"))
        val role1 = rolesRepo.insert(RoleCreate(app1.id, "role1"))
        val role2 = rolesRepo.insert(RoleCreate(app1.id, "role2"))
        rolesRepo.insert(RoleCreate(app1.id, "role3"))
        val group = groupsRepo.insert(GroupCreate("test-group")).block()!!
        val user = usersRepo.insert(UserCreate("user", "pass")).block()!!
        groupRolesRepo.insert(group.id, role1.id)
                .then(groupRolesRepo.insert(group.id, role2.id))
                .then(repo.insert(user.id, group.id)).block()

        StepVerifier.create(repo.findGroupRolesByUserId(user.id).collectList())
            .expectNextMatches { list -> list.size == 2 && list.contains(role1) && list.contains(role2) }
            .verifyComplete()

        return@runBlocking
    }
}
