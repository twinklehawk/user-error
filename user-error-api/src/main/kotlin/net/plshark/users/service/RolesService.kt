package net.plshark.users.service

import net.plshark.users.model.Role
import net.plshark.users.model.RoleCreate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for managing roles
 */
interface RolesService {
    /**
     * Retrieve a role by name
     * @param applicationId the ID of the application the role belongs to
     * @param name the role name
     * @return the matching role or empty if not found
     */
    operator fun get(applicationId: Long, name: String): Mono<Role>

    /**
     * Retrieve a role by name
     * @param applicationId the ID of the parent application
     * @param name the role name
     * @return the matching role or an [net.plshark.errors.ObjectNotFoundException] if not found
     */
    fun getRequired(applicationId: Long, name: String): Mono<Role>

    /**
     * Get all roles up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return the roles
     */
    fun getRoles(maxResults: Int, offset: Long): Flux<Role>

    /**
     * Save a new role
     * @param role the role
     * @return the saved role or a [net.plshark.errors.DuplicateException] if a role with the same name already
     * exists in the same application
     */
    fun create(role: RoleCreate): Mono<Role>

    /**
     * Delete a role
     * @param applicationId the parent application ID
     * @param name the role name
     * @return an empty result
     */
    fun delete(applicationId: Long, name: String): Mono<Void>

    /**
     * Delete a role
     * @param roleId the role ID
     * @return an empty result
     */
    fun delete(roleId: Long): Mono<Void>
}
