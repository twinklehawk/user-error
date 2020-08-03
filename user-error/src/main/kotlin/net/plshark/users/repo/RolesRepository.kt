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
     * Get a role by ID
     * @param id the ID
     * @return the matching role
     */
    operator fun get(id: Long): Mono<Role>

    /**
     * Get a role by name
     * @param applicationId the application the role belongs to
     * @param name the role name
     * @return the matching role
     */
    operator fun get(applicationId: Long, name: String): Mono<Role>

    /**
     * Get all roles up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return the roles
     */
    fun getRoles(maxResults: Int, offset: Long): Flux<Role>

    /**
     * Insert a new role
     * @param role the role to insert
     * @return the inserted role, will have the ID set
     */
    fun insert(role: RoleCreate): Mono<Role>

    /**
     * Delete a role by ID
     * @param id the role ID
     * @return an empty result
     */
    fun delete(id: Long): Mono<Void>

    /**
     * Get all roles belonging to an application
     * @param applicationId the application ID
     * @return the roles
     */
    fun getRolesForApplication(applicationId: Long): Flux<Role>
}
