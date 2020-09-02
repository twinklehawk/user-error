package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import net.plshark.testutils.DbIntTest
import net.plshark.users.model.*
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
    fun `deleting a group ID should delete all associations for that group`() {
        val group1 = groupsRepo.insert(GroupCreate("group1")).block()!!
        val group2 = groupsRepo.insert(GroupCreate("group2")).block()!!
        val user1 = usersRepo.insert(UserCreate("user1", "pass")).block()!!
        val user2 = usersRepo.insert(UserCreate("user2", "pass")).block()!!
        val user3 = usersRepo.insert(UserCreate("user3", "pass")).block()!!
        repo.insert(user1.id, group1.id)
            .then(repo.insert(user2.id, group1.id))
            .then(repo.insert(user2.id, group2.id))
            .then(repo.insert(user3.id, group2.id))
            .block()

        repo.deleteUserGroupsByGroupId(group1.id).block()

        assertEquals(0, repo.findGroupsByUserId(user1.id).count().block())
        assertEquals(listOf(group2), repo.findGroupsByUserId(user2.id).collectList().block())
        assertEquals(listOf(group2), repo.findGroupsByUserId(user3.id).collectList().block())
    }

    @Test
    fun `deleting a user ID should delete all associations for that user`() {
        val group1 = groupsRepo.insert(GroupCreate("group1")).block()!!
        val group2 = groupsRepo.insert(GroupCreate("group2")).block()!!
        val user1 = usersRepo.insert(UserCreate("user1", "pass")).block()!!
        val user2 = usersRepo.insert(UserCreate("user2", "pass")).block()!!
        repo.insert(user1.id, group1.id)
                .then(repo.insert(user1.id, group2.id))
                .then(repo.insert(user2.id, group1.id))
                .then(repo.insert(user2.id, group2.id))
                .block()

        repo.deleteUserGroupsByUserId(user1.id).block()

        assertEquals(0, repo.findGroupsByUserId(user1.id).count().block())
        assertEquals(listOf(group1, group2), repo.findGroupsByUserId(user2.id).collectList().block())
    }

    @Test
    fun `retrieving roles should return each role in each group the user belongs to`() {
        val app1 = appsRepo.insert(ApplicationCreate("test-app")).block()!!
        val role1 = rolesRepo.insert(RoleCreate(app1.id, "role1")).block()!!
        val role2 = rolesRepo.insert(RoleCreate(app1.id, "role2")).block()!!
        rolesRepo.insert(RoleCreate(app1.id, "role3")).block()
        val group = groupsRepo.insert(GroupCreate("test-group")).block()!!
        val user = usersRepo.insert(UserCreate("user", "pass")).block()!!
        groupRolesRepo.insert(group.id, role1.id)
                .then(groupRolesRepo.insert(group.id, role2.id))
                .then(repo.insert(user.id, group.id)).block()

        StepVerifier.create(repo.findGroupRolesByUserId(user.id).collectList())
            .expectNextMatches { list -> list.size == 2 && list.contains(role1) && list.contains(role2) }
            .verifyComplete()
    }
}
