package net.plshark.users.webservice

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.plshark.errors.BadRequestException
import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Group
import net.plshark.users.model.PasswordChangeRequest
import net.plshark.users.model.Role
import net.plshark.users.model.User
import net.plshark.users.model.UserCreate
import net.plshark.users.repo.UserGroupsRepository
import net.plshark.users.repo.UserRolesRepository
import net.plshark.users.repo.UsersRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.crypto.password.PasswordEncoder

@Suppress("ReactiveStreamsUnusedPublisher")
class UsersControllerTest {

    private val userRepo = mockk<UsersRepository>()
    private val userRolesRepo = mockk<UserRolesRepository>()
    private val userGroupsRepo = mockk<UserGroupsRepository>()
    private val encoder = mockk<PasswordEncoder>()
    private val controller = UsersController(userRepo, userRolesRepo, userGroupsRepo, encoder)

    @Test
    fun `new users have password encoded`() = runBlocking {
        every { encoder.encode("pass") } returns "pass-encoded"
        coEvery { userRepo.insert(UserCreate("user", "pass-encoded")) } returns
            User(1, "user")

        assertEquals(User(1, "user"), controller.create(UserCreate("user", "pass")))
    }

    @Test
    fun `an insert request is rejected if the password is empty`() {
        assertThrows<BadRequestException> {
            runBlocking {
                controller.create(UserCreate("user", ""))
            }
        }
    }

    @Test
    fun `create should map the exception for a duplicate username to a DuplicateException`() {
        val request = UserCreate("app", "pass")
        every { encoder.encode("pass") } returns "pass"
        coEvery { userRepo.insert(request) } throws DataIntegrityViolationException("test error")

        assertThrows<DuplicateException> {
            runBlocking {
                controller.create(request)
            }
        }
    }

    @Test
    fun `cannot update a user to have an empty password`() {
        assertThrows<BadRequestException> {
            runBlocking {
                controller.changePassword(123, PasswordChangeRequest("current", ""))
            }
        }
    }

    @Test
    fun `new password is encoded when updating password`() = runBlocking {
        every { encoder.encode("current") } returns "current-encoded"
        every { encoder.encode("new-pass") } returns "new-pass-encoded"
        coEvery { userRepo.findById(100) } returns User(100, "test")
        coEvery { userRepo.updatePassword(100, "current-encoded", "new-pass-encoded") } coAnswers { }

        controller.changePassword(100, PasswordChangeRequest("current", "new-pass"))

        coVerify { userRepo.updatePassword(100, "current-encoded", "new-pass-encoded") }
    }

    @Test
    fun `no matching user when updating password throws exception`() {
        every { encoder.encode("current") } returns "current-encoded"
        every { encoder.encode("new") } returns "new-encoded"
        coEvery { userRepo.findById(200) } returns null

        assertThrows<ObjectNotFoundException> {
            runBlocking {
                controller.changePassword(200, PasswordChangeRequest("current", "new"))
            }
        }
    }

    @Test
    fun `no matching existing password when updating password throws exception`() {
        every { encoder.encode("current") } returns "current-encoded"
        every { encoder.encode("new") } returns "new-encoded"
        coEvery { userRepo.findById(100) } returns User(100, "ted")
        coEvery { userRepo.updatePassword(100, "current-encoded", "new-encoded") } throws
            EmptyResultDataAccessException(1)

        assertThrows<BadRequestException> {
            runBlocking {
                controller.changePassword(100, PasswordChangeRequest("current", "new"))
            }
        }
    }

    @Test
    fun `the user is removed when the user is deleted`() = runBlocking {
        coEvery { userRepo.deleteById(100) } coAnswers { }
        controller.delete(100)
        coVerify { userRepo.deleteById(100) }
    }

    @Test
    fun `deleting by username deletes the user`() = runBlocking {
        coEvery { userRepo.deleteById(100) } coAnswers { }
        controller.delete(100)
        coVerify { userRepo.deleteById(100) }
    }

    @Test
    fun `getUsers should return all results`() = runBlocking {
        val user1 = User(1, "user")
        val user2 = User(2, "user2")
        every { userRepo.getAll(5, 0) } returns flowOf(user1, user2)

        val list = controller.getUsers(5, 0).toList()
        assertEquals(2, list.size)
        assertTrue(list.contains(user1))
        assertTrue(list.contains(user2))
    }

    @Test
    fun `getUserRoles should return all roles`() = runBlocking {
        val r1 = Role(1, 2, "role1")
        val r2 = Role(2, 2, "role2")
        coEvery { userRepo.findById(3) } returns User(3, "test-user")
        every { userRolesRepo.findRolesByUserId(3) } returns flowOf(r1, r2)

        val list = controller.getUserRoles(3).toList()
        assertEquals(2, list.size)
        assertTrue(list.contains(r1))
        assertTrue(list.contains(r2))
    }

    @Test
    fun `updateUserRoles passes through`() = runBlocking {
        val r1 = Role(1, 2, "role1")
        val r2 = Role(2, 2, "role2")
        val r3 = Role(3, 1, "role3")
        coEvery { userRepo.findById(123) } returns User(123, "test-user")
        every { userRolesRepo.findRolesByUserId(123) } returns flowOf(r1, r2)
        coEvery { userRolesRepo.deleteById(123, 2) } coAnswers { }
        coEvery { userRolesRepo.insert(123, 3) } coAnswers { }

        val roles = controller.updateUserRoles(123, setOf(r1, r3)).toList()
        assertEquals(2, roles.size)
        assertTrue(roles.contains(r1))
        assertTrue(roles.contains(r3))
    }

    @Test
    fun `getUserGroups passes through`() = runBlocking {
        val g1 = Group(1, "group1")
        val g2 = Group(2, "group2")
        coEvery { userRepo.findById(4) } returns User(4, "test-user")
        every { userGroupsRepo.findGroupsByUserId(4) } returns flowOf(g1, g2)

        val groups = controller.getUserGroups(4).toList()
        assertEquals(2, groups.size)
        assertTrue(groups.contains(g1))
        assertTrue(groups.contains(g2))
    }

    @Test
    fun `updateUserGroups passes through`() = runBlocking {
        val g1 = Group(1, "group1")
        val g2 = Group(2, "group2")
        val g3 = Group(3, "group3")
        coEvery { userRepo.findById(123) } returns User(123, "test-user")
        every { userGroupsRepo.findGroupsByUserId(123) } returns flowOf(g1, g2)
        coEvery { userGroupsRepo.deleteById(123, 2) } coAnswers { }
        coEvery { userGroupsRepo.insert(123, 3) } coAnswers { }

        val groups = controller.updateUserGroups(123, setOf(g1, g3)).toList()
        assertEquals(2, groups.size)
        assertTrue(groups.contains(g1))
        assertTrue(groups.contains(g3))
    }
}
