package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import kotlinx.coroutines.runBlocking
import net.plshark.testutils.DbIntTest
import net.plshark.users.model.ApplicationCreate
import net.plshark.users.model.Role
import net.plshark.users.model.RoleCreate
import net.plshark.users.model.User
import net.plshark.users.model.UserCreate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient

class SpringDataUserRolesRepositoryTest : DbIntTest() {

    private lateinit var repo: SpringDataUserRolesRepository
    private lateinit var usersRepo: SpringDataUsersRepository
    private lateinit var rolesRepo: SpringDataRolesRepository
    private lateinit var appsRepo: SpringDataApplicationsRepository
    private lateinit var testRole1: Role
    private lateinit var testRole2: Role
    private lateinit var user1: User
    private lateinit var user2: User

    @BeforeEach
    fun setup() = runBlocking {
        val connectionFactory = ConnectionFactories.get(DB_URL)
        val client = DatabaseClient.create(connectionFactory)
        repo = SpringDataUserRolesRepository(client)
        usersRepo = SpringDataUsersRepository(client)
        rolesRepo = SpringDataRolesRepository(client)
        appsRepo = SpringDataApplicationsRepository(client)

        val app = appsRepo.insert(ApplicationCreate("app"))
        testRole1 = rolesRepo.insert(RoleCreate(app.id, "testRole1")).block()!!
        testRole2 = rolesRepo.insert(RoleCreate(app.id, "testRole2")).block()!!
        user1 = usersRepo.insert(UserCreate("test-user", "test-pass")).block()!!
        user2 = usersRepo.insert(UserCreate("test-user2", "test-pass")).block()!!
    }

    @Test
    fun `can add a role to a user`() {
        repo.insert(user1.id, testRole1.id).block()

        repo.findRolesByUserId(user1.id).collectList().block()!!.stream().anyMatch{ role -> role.id == testRole1.id}
    }

    @Test
    fun `can retrieve all roles for a user`() {
        repo.insert(user1.id, testRole1.id).block()
        repo.insert(user1.id, testRole2.id).block()

        val roles = repo.findRolesByUserId(user1.id).collectList().block()!!

        assertEquals(2, roles.size)
        assertTrue(roles.stream().anyMatch{role -> role.id == testRole1.id})
        assertTrue(roles.stream().anyMatch{role -> role.id == testRole2.id})
    }

    @Test
    fun `retrieving roles for a user does not return roles for other users`() {
        repo.insert(user1.id, testRole1.id).block()
        repo.insert(user2.id, testRole2.id).block()

        val roles = repo.findRolesByUserId(user1.id).collectList().block()!!

        assertEquals(1, roles.size)
        assertTrue(roles.stream().anyMatch{role -> role.id == testRole1.id})
    }

    @Test
    fun `can delete an existing user role`() {
        repo.insert(user1.id, testRole1.id).block()

        repo.deleteById(user1.id, testRole1.id).block()

        assertEquals(0, repo.findRolesByUserId(user1.id).collectList().block()!!.size)
    }

    @Test
    fun `deleting a user role that does not exist does not throw an exception`() {
        repo.deleteById(user1.id, 200).block()
    }
}
