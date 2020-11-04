package net.plshark.users.webservice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Role
import net.plshark.users.model.RoleCreate
import net.plshark.users.repo.RolesRepository
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
 * Controller to provide web service methods for roles
 */
@RestController
@RequestMapping("/applications/{applicationId}/roles")
class RolesController(private val rolesRepo: RolesRepository) {

    @PostMapping(
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun create(
        @PathVariable("applicationId") applicationId: Long,
        @RequestBody role: String
    ): Role {
        // TODO return 404 when application does not exist
        // TODO make this method accept a RoleCreate object
        try {
            return rolesRepo.insert(RoleCreate(applicationId, role))
        } catch (e: DataIntegrityViolationException) {
            throw DuplicateException("A role with name $role already exists", e)
        }
    }

    @DeleteMapping(path = ["/{roleId}"])
    suspend fun delete(@PathVariable("roleId") roleId: Long) {
        // TODO care about app ID
        rolesRepo.deleteById(roleId)
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findRolesByApplication(
        @PathVariable("applicationId") applicationId: Long,
        @RequestParam(value = "max-results", defaultValue = "50") maxResults: @Min(1) Int,
        @RequestParam(value = "offset", defaultValue = "0") offset: @Min(0) Int
    ): Flow<Role> {
        // TODO better pagination
        return rolesRepo.findRolesByApplicationId(applicationId)
            .drop(offset)
            .take(maxResults)
    }

    @GetMapping(path = ["/{roleId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findById(
        @PathVariable("applicationId") applicationId: Long,
        @PathVariable("roleId") roleId: Long
    ): Role {
        val role = rolesRepo.findById(roleId)
        if (role == null || role.applicationId != applicationId) throw ObjectNotFoundException(
            "No role found for $roleId"
        )
        return role
    }
}
