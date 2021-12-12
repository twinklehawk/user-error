package net.plshark.usererror.user.springdata

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.testutil.DbTest
import net.plshark.usererror.user.ApplicationCreate
import net.plshark.usererror.user.Role
import net.plshark.usererror.user.RoleCreate
import net.plshark.usererror.user.User
import net.plshark.usererror.user.UserCreate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.r2dbc.core.DatabaseClient

@DbTest
class SpringDataUserRolesRepositoryTest {

    private lateinit var repo: SpringDataUserRolesRepository
    private lateinit var usersRepo: SpringDataUsersRepository
    private lateinit var rolesRepo: SpringDataRolesRepository
    private lateinit var appsRepo: SpringDataApplicationsRepository
    private lateinit var testRole1: Role
    private lateinit var testRole2: Role
    private lateinit var user1: User
    private lateinit var user2: User

    @BeforeEach
    fun setup(client: DatabaseClient) = runBlocking {
        repo = SpringDataUserRolesRepository(client)
        usersRepo = SpringDataUsersRepository(client)
        rolesRepo = SpringDataRolesRepository(client)
        appsRepo = SpringDataApplicationsRepository(client)

        val app = appsRepo.insert(ApplicationCreate("app"))
        testRole1 = rolesRepo.insert(RoleCreate(app.id, "testRole1"))
        testRole2 = rolesRepo.insert(RoleCreate(app.id, "testRole2"))
        user1 = usersRepo.insert(UserCreate("test-user", "test-pass"))
        user2 = usersRepo.insert(UserCreate("test-user2", "test-pass"))
    }

    @Test
    fun `can add a role to a user`() = runBlocking {
        repo.insert(user1.id, testRole1.id)

        assertTrue(repo.findRolesByUserId(user1.id).toList().stream().anyMatch { role -> role.id == testRole1.id })
    }

    @Test
    fun `can retrieve all roles for a user`() = runBlocking {
        repo.insert(user1.id, testRole1.id)
        repo.insert(user1.id, testRole2.id)

        val roles = repo.findRolesByUserId(user1.id).toList()

        assertEquals(2, roles.size)
        assertTrue(roles.stream().anyMatch { role -> role.id == testRole1.id })
        assertTrue(roles.stream().anyMatch { role -> role.id == testRole2.id })
    }

    @Test
    fun `retrieving roles for a user does not return roles for other users`() = runBlocking {
        repo.insert(user1.id, testRole1.id)
        repo.insert(user2.id, testRole2.id)

        val roles = repo.findRolesByUserId(user1.id).toList()

        assertEquals(1, roles.size)
        assertTrue(roles.stream().anyMatch { role -> role.id == testRole1.id })
    }

    @Test
    fun `can delete an existing user role`() = runBlocking {
        repo.insert(user1.id, testRole1.id)

        repo.deleteById(user1.id, testRole1.id)

        assertEquals(0, repo.findRolesByUserId(user1.id).toList().size)
    }

    @Test
    fun `deleting a user role that does not exist does not throw an exception`() = runBlocking {
        repo.deleteById(user1.id, 200)
    }
}
