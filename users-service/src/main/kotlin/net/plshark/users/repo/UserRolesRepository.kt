package net.plshark.users.repo

import net.plshark.users.model.Role
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository for adding and removing roles for users
 */
interface UserRolesRepository {
    /**
     * Get all the roles for a user
     * @param userId the user ID
     * @return the roles for that user
     */
    fun getRolesForUser(userId: Long): Flux<Role>

    /**
     * Grant a role to a user
     * @param userId the ID of the user to grant the role to
     * @param roleId the ID of the role to grant
     * @return an empty result
     */
    fun insert(userId: Long, roleId: Long): Mono<Void>

    /**
     * Remove a role to a user
     * @param userId the ID of the user to remove the role from
     * @param roleId the ID of the role to remove
     * @return an empty result
     */
    fun delete(userId: Long, roleId: Long): Mono<Void>

    /**
     * Delete all user roles for a user
     * @param userId the user ID
     * @return an empty result
     */
    fun deleteUserRolesForUser(userId: Long): Mono<Void>

    /**
     * Delete all user roles for a role
     * @param roleId the role ID
     * @return an empty result
     */
    fun deleteUserRolesForRole(roleId: Long): Mono<Void>
}