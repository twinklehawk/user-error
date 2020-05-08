package net.plshark.users.webservice

import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Role
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
import javax.validation.constraints.Min

/**
 * Controller to provide web service methods for roles
 */
@RestController
@RequestMapping("/applications/{application}/roles")
class RolesController(private val rolesService: RolesService) {

    /**
     * Insert a new role
     * @param application the application the role will belong to
     * @param role the role to insert
     * @return the inserted role
     */
    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(
        @PathVariable("application") application: String,
        @RequestBody role: Role
    ): Mono<Role> {
        return rolesService.create(application, role)
    }

    /**
     * Delete a role
     * @param application the name of the parent application
     * @param name the role name
     * @return an empty result
     */
    @DeleteMapping(path = ["/{name}"])
    fun delete(
        @PathVariable("application") application: String,
        @PathVariable("name") name: String
    ): Mono<Void> {
        return rolesService.delete(application, name)
    }

    /**
     * Get all roles up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at
     * @return the roles
     */
    // TODO should only get roles belonging to application
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getRoles(
        @RequestParam(value = "max-results", defaultValue = "50") maxResults: @Min(1) Int,
        @RequestParam(value = "offset", defaultValue = "0") offset: @Min(0) Long
    ): Flux<Role> {
        return rolesService.getRoles(maxResults, offset)
    }

    /**
     * Get a role
     * @param application the parent application name
     * @param name the name of the role
     * @return the matching role if found
     */
    @GetMapping(path = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    operator fun get(
        @PathVariable("application") application: String,
        @PathVariable("name") name: String
    ): Mono<Role> {
        return rolesService[application, name]
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No role found for $application:$name") })
    }

}