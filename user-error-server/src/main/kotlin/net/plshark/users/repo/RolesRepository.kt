package net.plshark.users.repo

import net.plshark.users.model.Role
import net.plshark.users.model.RoleCreate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository for saving, deleting, and retrieving roles
 */
interface RolesRepository {

    /**
     * Find a role by ID
     * @param id the ID
     * @return a [Mono] emitting the matching role or empty if not found
     */
    fun findById(id: Long): Mono<Role>

    /**
     * Find a role by name
     * @param applicationId the parent application ID
     * @param name the role name
     * @return a [Mono] emitting the matching role or empty if not found
     */
    fun findByApplicationIdAndName(applicationId: Long, name: String): Mono<Role>

    /**
     * Find all roles up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return a [Flux] emitting the roles
     */
    fun getRoles(maxResults: Int, offset: Long): Flux<Role>

    /**
     * Find all roles belonging to an application
     * @param applicationId the application ID
     * @return a [Flux] emitting the roles
     */
    fun findRolesByApplicationId(applicationId: Long): Flux<Role>

    /**
     * Insert a new role
     * @param role the role to insert
     * @return a [Mono] emitting the inserted role
     */
    fun insert(role: RoleCreate): Mono<Role>

    /**
     * Delete a role by ID
     * @param id the role ID
     * @return a [Mono] signalling when complete
     */
    fun deleteById(id: Long): Mono<Void>
}
