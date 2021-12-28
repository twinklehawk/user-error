package net.plshark.usererror.role

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.error.DuplicateException
import net.plshark.usererror.error.NotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.DataIntegrityViolationException

class GroupsControllerTest {

    private val groupsRepo = mockk<GroupsRepository>()
    private val groupRolesRepo = mockk<GroupRolesRepository>()
    private val controller = GroupsController(groupsRepo, groupRolesRepo)

    @Test
    fun `should be able to retrieve groups by ID`() = runBlocking {
        val group = Group(123, "group-name")
        coEvery { groupsRepo.findById(123) } returns group

        assertEquals(group, controller.findById(123))
    }

    @Test
    fun `getting a group should throw an exception when the group does not exist`() {
        coEvery { groupsRepo.findById(2) } returns null

        assertThrows<NotFoundException> {
            runBlocking {
                controller.findById(2)
            }
        }
    }

    @Test
    fun `should be able to retrieve all groups`() = runBlocking {
        val g1 = Group(123, "group1")
        val g2 = Group(321, "group2")
        every { groupsRepo.getGroups(2, 0) } returns flowOf(g1, g2)

        assertEquals(listOf(g1, g2), controller.getAll(2, 0).toList())
    }

    @Test
    fun `should be able to retrieve all roles in a group`() = runBlocking {
        val r1 = Role(1, 2, "group1")
        val r2 = Role(2, 2, "group2")
        coEvery { groupRolesRepo.findRolesForGroup(100) } returns flow {
            emit(r1)
            emit(r2)
        }

        val list = controller.getRolesInGroup(100).toList()
        assertEquals(2, list.size)
        assertEquals(r1, list[0])
        assertEquals(r2, list[1])
    }

    @Test
    fun `creating should save and return the saved group`() = runBlocking {
        val request = GroupCreate("group")
        val inserted = Group(1, "group")
        coEvery { groupsRepo.insert(request) } returns inserted

        assertEquals(inserted, controller.create(request))
    }

    @Test
    fun `create should map the exception for a duplicate name to a DuplicateException`() {
        val request = GroupCreate("app")
        coEvery { groupsRepo.insert(request) } throws DataIntegrityViolationException("test error")

        assertThrows<DuplicateException> {
            runBlocking {
                controller.create(request)
            }
        }
    }

    @Test
    fun `deleting should delete the group`() {
        coEvery { groupsRepo.deleteById(100) } coAnswers { }
        runBlocking { controller.delete(100) }
        coVerify { groupsRepo.deleteById(100) }
    }

    @Test
    fun `should be able to add a role to a group`() {
        coEvery { groupRolesRepo.insert(1, 2) } coAnswers { }
        runBlocking { controller.addRoleToGroup(1, 2) }
        coVerify { groupRolesRepo.insert(1, 2) }
    }

    @Test
    fun `should be able to remove a role from a group`() {
        coEvery { groupRolesRepo.deleteById(1, 2) } coAnswers { }
        runBlocking { controller.removeRoleFromGroup(1, 2) }
        coVerify { groupRolesRepo.deleteById(1, 2) }
    }
}
