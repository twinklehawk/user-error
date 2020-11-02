package net.plshark.users.webservice

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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.constraints.Min

/**
 * Controller to provide web service methods for roles
 */
@RestController
@RequestMapping("/applications/{applicationId}/roles")
class RolesController(private val rolesRepo: RolesRepository) {

    /**
     * Insert a new role
     * @param applicationId the ID of the parent application
     * @param role the role to insert
     * @return the inserted role
     */
    @PostMapping(
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(
        @PathVariable("applicationId") applicationId: Long,
        @RequestBody role: String
    ): Mono<Role> {
        // TODO return 404 when application does not exist
        // TODO make this method accept a RoleCreate object
        return rolesRepo.insert(RoleCreate(applicationId, role))
            .onErrorMap(DataIntegrityViolationException::class.java) { e: DataIntegrityViolationException? ->
                DuplicateException("A role with name $role already exists", e)
            }
    }

    /**
     * Delete a role
     * @param roleId the role ID
     * @return an empty result
     */
    @DeleteMapping(path = ["/{roleId}"])
    fun delete(@PathVariable("roleId") roleId: Long): Mono<Void> {
        // TODO care about app ID
        return rolesRepo.deleteById(roleId)
    }

    /**
     * Get roles belonging to an application
     * @param applicationId the application ID
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start at, 0 to start at the beginning
     * @return a [Flux] emitting the roles
     */
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findRolesByApplication(
        @PathVariable("applicationId") applicationId: Long,
        @RequestParam(value = "max-results", defaultValue = "50") maxResults: @Min(1) Int,
        @RequestParam(value = "offset", defaultValue = "0") offset: @Min(0) Long
    ): Flux<Role> {
        // TODO better pagination
        return rolesRepo.findRolesByApplicationId(applicationId)
            .skip(offset)
            .take(maxResults.toLong())
    }

    /**
     * Get a role
     * @param applicationId the ID of the parent application name
     * @param roleId the ID of the role
     * @return the matching role if found
     */
    @GetMapping(path = ["/{roleId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(
        @PathVariable("applicationId") applicationId: Long,
        @PathVariable("roleId") roleId: Long
    ): Mono<Role> {
        return rolesRepo.findById(roleId)
            .filter { it.applicationId == applicationId }
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No role found for $roleId") })
    }
}
