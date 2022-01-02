package net.plshark.usererror.server.role.impl

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.role.ApplicationCreate
import net.plshark.usererror.role.Role
import net.plshark.usererror.role.RoleCreate
import net.plshark.usererror.server.testutil.DbTest
import net.plshark.usererror.server.user.impl.UsersRepositoryImpl
import net.plshark.usererror.user.User
import net.plshark.usererror.user.UserCreate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.r2dbc.core.DatabaseClient

@DbTest
class UserRolesRepositoryImplTest {

    private lateinit var repo: UserRolesRepositoryImpl
    private lateinit var usersRepo: UsersRepositoryImpl
    private lateinit var rolesRepo: RolesRepositoryImpl
    private lateinit var appsRepo: ApplicationsRepositoryImpl
    private lateinit var testRole1: Role
    private lateinit var testRole2: Role
    private lateinit var user1: User
    private lateinit var user2: User

    @BeforeEach
    fun setup(client: DatabaseClient) = runBlocking {
        repo = UserRolesRepositoryImpl(client)
        usersRepo = UsersRepositoryImpl(client)
        rolesRepo = RolesRepositoryImpl(client)
        appsRepo = ApplicationsRepositoryImpl(client)

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
