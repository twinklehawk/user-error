package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import net.plshark.testutils.DbIntTest
import net.plshark.users.model.Application
import net.plshark.users.model.Group
import net.plshark.users.model.Role
import net.plshark.users.model.User
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
        val group = groupsRepo.insert(Group(null, "test-name")).block()!!
        val user = usersRepo.insert(User(null, "test-user", "pass")).block()!!

        repo.insert(user.id!!, group.id!!).block()

        assertEquals(listOf(group), repo.getGroupsForUser(user.id!!).collectList().block())
    }

    @Test
    fun `retrieving should return empty when no users are assigned to the group`() {
        assertEquals(0, repo.getGroupsForUser(123).count().block())
    }

    @Test
    fun `delete should delete a group-user association`() {
        val group = groupsRepo.insert(Group(null, "test-group")).block()!!
        val user = usersRepo.insert(User(null, "test-user", "pass")).block()!!
        repo.insert(user.id!!, group.id!!).block()

        repo.delete(user.id!!, group.id!!).block()

        assertEquals(0, repo.getGroupsForUser(user.id!!).count().block())
    }

    @Test
    fun `delete should not throw an exception if the group-user association does not already exist`() {
        repo.delete(100, 200).block()
    }

    @Test
    fun `deleting a group ID should delete all associations for that group`() {
        val group1 = groupsRepo.insert(Group(null, "group1")).block()!!
        val group2 = groupsRepo.insert(Group(null, "group2")).block()!!
        val user1 = usersRepo.insert(User(null, "user1", "pass")).block()!!
        val user2 = usersRepo.insert(User(null, "user2", "pass")).block()!!
        val user3 = usersRepo.insert(User(null, "user3", "pass")).block()!!
        repo.insert(user1.id!!, group1.id!!)
            .then(repo.insert(user2.id!!, group1.id!!))
            .then(repo.insert(user2.id!!, group2.id!!))
            .then(repo.insert(user3.id!!, group2.id!!))
            .block()

        repo.deleteUserGroupsForGroup(group1.id!!).block()

        assertEquals(0, repo.getGroupsForUser(user1.id!!).count().block())
        assertEquals(listOf(group2), repo.getGroupsForUser(user2.id!!).collectList().block())
        assertEquals(listOf(group2), repo.getGroupsForUser(user3.id!!).collectList().block())
    }

    @Test
    fun `deleting a user ID should delete all associations for that user`() {
        val group1 = groupsRepo.insert(Group(null, "group1")).block()!!
        val group2 = groupsRepo.insert(Group(null, "group2")).block()!!
        val user1 = usersRepo.insert(User(null, "user1", "pass")).block()!!
        val user2 = usersRepo.insert(User(null, "user2", "pass")).block()!!
        repo.insert(user1.id!!, group1.id!!)
                .then(repo.insert(user1.id!!, group2.id!!))
                .then(repo.insert(user2.id!!, group1.id!!))
                .then(repo.insert(user2.id!!, group2.id!!))
                .block()

        repo.deleteUserGroupsForUser(user1.id!!).block()

        assertEquals(0, repo.getGroupsForUser(user1.id!!).count().block())
        assertEquals(listOf(group1, group2), repo.getGroupsForUser(user2.id!!).collectList().block())
    }

    @Test
    fun `retrieving roles should return each role in each group the user belongs to`() {
        val app1 = appsRepo.insert(Application(null, "test-app")).block()!!
        val role1 = rolesRepo.insert(Role(null, app1.id, "role1")).block()!!
        val role2 = rolesRepo.insert(Role(null, app1.id, "role2")).block()!!
        rolesRepo.insert(Role(null, app1.id, "role3")).block()
        val group = groupsRepo.insert(Group(null, "test-group")).block()!!
        val user = usersRepo.insert(User(null, "user", "pass")).block()!!
        groupRolesRepo.insert(group.id!!, role1.id!!)
                .then(groupRolesRepo.insert(group.id!!, role2.id!!))
                .then(repo.insert(user.id!!, group.id!!)).block()

        StepVerifier.create(repo.getGroupRolesForUser(user.id!!).collectList())
            .expectNextMatches { list -> list.size == 2 && list.contains(role1) && list.contains(role2) }
            .verifyComplete()
    }
}
