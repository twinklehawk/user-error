package net.plshark.users.repo

import net.plshark.users.model.Role
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository for adding and removing roles for users
 */
interface UserRolesRepository {

    /**
     * Find all the roles for a user
     * @param userId the user ID
     * @return a [Flux] emitting the roles for the user
     */
    fun findRolesByUserId(userId: Long): Flux<Role>

    /**
     * Grant a role to a user
     * @param userId the ID of the user to grant the role to
     * @param roleId the ID of the role to grant
     * @return a [Mono] signalling when complete
     */
    fun insert(userId: Long, roleId: Long): Mono<Void>

    /**
     * Remove a role from a user
     * @param userId the ID of the user to remove the role from
     * @param roleId the ID of the role to remove
     * @return a [Mono] signalling when complete
     */
    fun deleteById(userId: Long, roleId: Long): Mono<Void>

    // TODO delete with group versions
    /**
     * Delete all user roles for a user
     * @param userId the user ID
     * @return a [Mono] signalling when complete
     */
    fun deleteUserRolesByUserId(userId: Long): Mono<Void>

    /**
     * Delete all user roles for a role
     * @param roleId the role ID
     * @return a [Mono] signalling when complete
     */
    fun deleteUserRolesByRoleId(roleId: Long): Mono<Void>
}
