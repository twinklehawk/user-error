package net.plshark.usererror.role

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.error.DuplicateException
import net.plshark.usererror.error.ObjectNotFoundException
import net.plshark.usererror.user.Role
import net.plshark.usererror.user.RoleCreate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.DataIntegrityViolationException

@Suppress("ReactiveStreamsUnusedPublisher")
class RolesControllerTest {

    private val rolesRepo = mockk<RolesRepository>()
    private val controller = RolesController(rolesRepo)

    @Test
    fun `creating should save a role and return the saved role`() = runBlocking {
        val role = RoleCreate(123, "role")
        val inserted = Role(1, 123, "role")
        coEvery { rolesRepo.insert(role) } returns inserted

        assertEquals(inserted, controller.create(123, "role"))
    }

    @Test
    fun `create should map the exception for a duplicate name to a DuplicateException`() {
        val request = RoleCreate(321, "app")
        coEvery { rolesRepo.insert(request.copy(applicationId = 321)) } throws
            DataIntegrityViolationException("test error")

        assertThrows<DuplicateException> {
            runBlocking {
                controller.create(321, "app")
            }
        }
    }

    @Test
    fun `deleting a role should delete the role`() {
        coEvery { rolesRepo.deleteById(100) } coAnswers { }
        runBlocking { rolesRepo.deleteById(100) }
        coVerify { rolesRepo.deleteById(100) }
    }

    @Test
    fun `should be able to retrieve a role by ID`() = runBlocking {
        val role = Role(123, 132, "role-name")
        coEvery { rolesRepo.findById(123) } returns role

        assertEquals(role, controller.findById(132, 123))
    }

    @Test
    fun `getting a role should throw an exception when the role does not exist`() {
        coEvery { rolesRepo.findById(5) } returns null

        assertThrows<ObjectNotFoundException> {
            runBlocking {
                controller.findById(4, 5)
            }
        }
    }

    @Test
    fun `should be able to retrieve all roles`() = runBlocking {
        val role1 = Role(1, 2, "role1")
        val role2 = Role(2, 2, "role2")
        coEvery { rolesRepo.findRolesByApplicationId(2, 100, 0) } returns flow {
            emit(role1)
            emit(role2)
        }

        val list = controller.findRolesByApplication(2, 100, 0).toList()
        assertEquals(2, list.size)
        assertEquals(role1, list[0])
        assertEquals(role2, list[1])
    }
}
