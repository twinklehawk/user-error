package net.plshark.users.repo

import net.plshark.users.model.Role
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository for group-role associations
 */
interface GroupRolesRepository {
    /**
     * Get roles belonging to a group
     * @param groupId the ID of the group
     * @returns all the roles belonging to the group
     */
    fun getRolesForGroup(groupId: Long): Flux<Role>

    /**
     * Add a new group-role association
     * @param groupId the group ID
     * @param roleId the role ID
     * @returns an empty Mono to indicate completion
     */
    fun insert(groupId: Long, roleId: Long): Mono<Void>

    /**
     * Remove a new group-role association
     * @param groupId the group ID
     * @param roleId the role ID
     * @returns an empty Mono to indicate completion
     */
    fun delete(groupId: Long, roleId: Long): Mono<Void>

    /**
    * Remove all associations for a group
    * @param groupId the group ID
    * @returns an empty Mono to indicate completion
    */
    fun deleteForGroup(groupId: Long): Mono<Void>

    /**
     * Remove all associations for a role
     * @param roleId the role ID
     * @returns an empty Mono to indicate completion
     */
    fun deleteForRole(roleId: Long): Mono<Void>
}