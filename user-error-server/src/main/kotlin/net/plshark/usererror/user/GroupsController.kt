package net.plshark.usererror.user

import kotlinx.coroutines.flow.Flow
import net.plshark.usererror.error.DuplicateException
import net.plshark.usererror.error.ObjectNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Min

/**
 * Controller to provide web service methods for groups
 */
@RestController
@RequestMapping("/groups")
class GroupsController(private val groupsRepo: GroupsRepository, private val groupRolesRepo: GroupRolesRepository) {

    @GetMapping(path = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findById(@PathVariable("id") id: Long): Group {
        return groupsRepo.findById(id) ?: throw ObjectNotFoundException("No group found for $id")
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAll(
        @RequestParam(value = "limit", defaultValue = "50") @Min(1) limit: Int,
        @RequestParam(value = "offset", defaultValue = "0") @Min(0) offset: Int
    ): Flow<Group> {
        return groupsRepo.getGroups(limit, offset)
    }

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun create(@RequestBody group: GroupCreate): Group {
        try {
            return groupsRepo.insert(group)
        } catch (e: DataIntegrityViolationException) {
            throw DuplicateException("A group with name ${group.name} already exists", e)
        }
    }

    @DeleteMapping(path = ["/{id}"])
    suspend fun delete(@PathVariable("id") id: Long) {
        groupsRepo.deleteById(id)
    }

    @PostMapping(path = ["/{id}/roles"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun addRoleToGroup(@PathVariable("id") groupId: Long, roleId: Long) {
        // TODO return 404 if group or role not found
        groupRolesRepo.insert(groupId, roleId)
    }

    @DeleteMapping(path = ["/{id}/roles/{roleId}"])
    suspend fun removeRoleFromGroup(
        @PathVariable("id") groupId: Long,
        @PathVariable("roleId") roleId: Long
    ) {
        groupRolesRepo.deleteById(groupId, roleId)
    }

    @GetMapping(path = ["/{id}/roles"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getRolesInGroup(@PathVariable("id") groupId: Long): Flow<Role> {
        return groupRolesRepo.findRolesForGroup(groupId)
    }
}
