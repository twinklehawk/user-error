package net.plshark.users.repo

import net.plshark.users.model.Role
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository for group-role associations
 */
interface GroupRolesRepository {

    /**
     * Find roles belonging to a group
     * @param groupId the ID of the group
     * @returns a [Flux] emitting all roles belonging to the group
     */
    fun findRolesForGroup(groupId: Long): Flux<Role>

    /**
     * Add a new group-role association
     * @param groupId the group ID
     * @param roleId the role ID
     * @returns a [Mono] signalling when complete
     */
    fun insert(groupId: Long, roleId: Long): Mono<Void>

    /**
     * Remove a group-role association
     * @param groupId the group ID
     * @param roleId the role ID
     * @returns a [Mono] signalling when complete
     */
    fun deleteById(groupId: Long, roleId: Long): Mono<Void>

    /**
    * Remove all associations for a group
    * @param groupId the group ID
    * @returns a [Mono] signalling when complete
    */
    fun deleteByGroupId(groupId: Long): Mono<Void>

    /**
     * Remove all associations for a role
     * @param roleId the role ID
     * @returns a [Mono] signalling when complete
     */
    fun deleteByRoleId(roleId: Long): Mono<Void>
}
