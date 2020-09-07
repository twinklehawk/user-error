package net.plshark.users.webservice

import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Role
import net.plshark.users.model.RoleCreate
import net.plshark.users.service.ApplicationsService
import net.plshark.users.service.RolesService
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
import reactor.kotlin.core.publisher.switchIfEmpty
import javax.validation.constraints.Min

/**
 * Controller to provide web service methods for roles
 */
@RestController
@RequestMapping("/applications/{applicationId}/roles")
class RolesController(private val rolesService: RolesService, private val appService: ApplicationsService) {

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
        return appService.findById(applicationId)
            .switchIfEmpty { Mono.error(ObjectNotFoundException("Application $applicationId not found")) }
            .map { RoleCreate(it.id, role) }
            .flatMap { rolesService.create(it) }
    }

    /**
     * Delete a role
     * @param applicationId the ID of the parent application
     * @param roleId the role ID
     * @return an empty result
     */
    @DeleteMapping(path = ["/{roleId}"])
    fun delete(
        @PathVariable("applicationId") applicationId: Long,
        @PathVariable("roleId") roleId: Long
    ): Mono<Void> {
        return rolesService.deleteById(roleId)
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
        return rolesService.findRolesByApplicationId(applicationId, maxResults, offset)
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
        return rolesService.findById(roleId)
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No role found for $roleId") })
    }
}
