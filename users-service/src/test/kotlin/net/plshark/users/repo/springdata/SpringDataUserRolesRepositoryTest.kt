package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import net.plshark.testutils.PlsharkFlywayPreparer
import net.plshark.users.model.Application
import net.plshark.users.model.Role
import net.plshark.users.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SpringDataUserRolesRepositoryTest {

    //@Rule
    val dbRule = EmbeddedPostgresRules.preparedDatabase(PlsharkFlywayPreparer.defaultPreparer())

    private lateinit var repo: SpringDataUserRolesRepository
    private lateinit var usersRepo: SpringDataUsersRepository
    private lateinit var rolesRepo: SpringDataRolesRepository
    private lateinit var appsRepo: SpringDataApplicationsRepository
    private lateinit var testRole1: Role
    private lateinit var testRole2: Role
    private lateinit var user1: User
    private lateinit var user2: User

    @BeforeEach
    fun setup() {
        val client = DatabaseClientHelper.buildTestClient(dbRule)
        repo = SpringDataUserRolesRepository(client)
        usersRepo = SpringDataUsersRepository(client)
        rolesRepo = SpringDataRolesRepository(client)
        appsRepo = SpringDataApplicationsRepository(client)

        val app = appsRepo.insert(Application(null, "app")).block()!!
        testRole1 = rolesRepo.insert(Role(null, app.id, "testRole1")).block()!!
        testRole2 = rolesRepo.insert(Role(null, app.id, "testRole2")).block()!!
        user1 = usersRepo.insert(User(null, "test-user", "test-pass")).block()!!
        user2 = usersRepo.insert(User(null, "test-user2", "test-pass")).block()!!
    }

    @Test
    fun `can add a role to a user`() {
        repo.insert(user1.id!!, testRole1.id!!).block()

        repo.getRolesForUser(user1.id!!).collectList().block()!!.stream().anyMatch{role -> role.id == testRole1.id}
    }

    @Test
    fun `can retrieve all roles for a user`() {
        repo.insert(user1.id!!, testRole1.id!!).block()
        repo.insert(user1.id!!, testRole2.id!!).block()

        val roles = repo.getRolesForUser(user1.id!!).collectList().block()!!

        assertEquals(2, roles.size)
        assertTrue(roles.stream().anyMatch{role -> role.id == testRole1.id})
        assertTrue(roles.stream().anyMatch{role -> role.id == testRole2.id})
    }

    @Test
    fun `retrieving roles for a user does not return roles for other users`() {
        repo.insert(user1.id!!, testRole1.id!!).block()
        repo.insert(user2.id!!, testRole2.id!!).block()

        val roles = repo.getRolesForUser(user1.id!!).collectList().block()!!

        assertEquals(1, roles.size)
        assertTrue(roles.stream().anyMatch{role -> role.id == testRole1.id})
    }

    @Test
    fun `can delete an existing user role`() {
        repo.insert(user1.id!!, testRole1.id!!).block()

        repo.delete(user1.id!!, testRole1.id!!).block()

        assertEquals(0, repo.getRolesForUser(user1.id!!).collectList().block()!!.size)
    }

    @Test
    fun `deleting a user role that does not exist does not throw an exception`() {
        repo.delete(user1.id!!, 200).block()
    }

    @Test
    fun `can delete all roles for a user`() {
        repo.insert(user1.id!!, testRole1.id!!).block()
        repo.insert(user1.id!!, testRole2.id!!).block()

        repo.deleteUserRolesForUser(user1.id!!).block()

        assertEquals(0, repo.getRolesForUser(user1.id!!).collectList().block()!!.size)
    }

    @Test
    fun `deleting all roles for a user does not affect other users`() {
        repo.insert(user1.id!!, testRole1.id!!).block()
        repo.insert(user2.id!!, testRole2.id!!).block()

        repo.deleteUserRolesForUser(user1.id!!).block()

        assertEquals(0, repo.getRolesForUser(user1.id!!).collectList().block()!!.size)
        assertEquals(1, repo.getRolesForUser(user2.id!!).collectList().block()!!.size)
    }

    @Test
    fun `can remove a role from all users`() {
        repo.insert(user1.id!!, testRole1.id!!).block()
        repo.insert(user2.id!!, testRole1.id!!).block()

        repo.deleteUserRolesForRole(testRole1.id!!).block()

        assertEquals(0, repo.getRolesForUser(user1.id!!).collectList().block()!!.size)
        assertEquals(0, repo.getRolesForUser(user2.id!!).collectList().block()!!.size)
    }

    @Test
    fun `removing a role from all users does not affect other roles`() {
        repo.insert(user1.id!!, testRole1.id!!).block()
        repo.insert(user2.id!!, testRole2.id!!).block()

        repo.deleteUserRolesForRole(testRole1.id!!).block()

        assertEquals(0, repo.getRolesForUser(user1.id!!).collectList().block()!!.size)
        assertEquals(1, repo.getRolesForUser(user2.id!!).collectList().block()!!.size)
    }
}
