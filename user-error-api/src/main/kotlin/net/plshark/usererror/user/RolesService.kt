package net.plshark.usererror.user

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for managing roles
 */
interface RolesService {

    /**
     * Find a role by ID
     * @param roleId the role ID
     * @return a [Mono] emitting the matching role or empty if not found
     */
    fun findById(roleId: Long): Mono<Role>

    /**
     * Find a role by ID
     * @param roleId the role ID
     * @return a [Mono] emitting the matching role or an [net.plshark.errors.ObjectNotFoundException] if not found
     */
    fun findRequiredById(roleId: Long): Mono<Role>

    /**
     * Get roles belonging to an application
     * @param applicationId the application ID
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start at, 0 to start at the beginning
     * @return a [Flux] emitting the roles
     */
    fun findRolesByApplicationId(applicationId: Long, maxResults: Int, offset: Long): Flux<Role>

    /**
     * Save a new role
     * @param role the role
     * @return a [Mono] emitting the saved role or a [net.plshark.errors.DuplicateException] if a role with the same
     * name already exists in the same application
     */
    fun create(role: RoleCreate): Mono<Role>

    /**
     * Delete a role by ID
     * @param roleId the role ID
     * @return a [Mono] signalling when complete
     */
    fun deleteById(roleId: Long): Mono<Void>
}
