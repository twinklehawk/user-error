package net.plshark.users.repo

import net.plshark.users.model.Group
import net.plshark.users.model.Role
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserGroupsRepository {
    /**
     * Get all the groups a user belongs to
     * @param userId the ID of the user
     * @return the groups
     */
    fun getGroupsForUser(userId: Long): Flux<Group>

    /**
     * Get all roles a user has through the groups a user belongs to
     * @param userId the ID of the user
     * @return the roles
     */
    fun getGroupRolesForUser(userId: Long): Flux<Role>

    /**
     * Add a user to a group
     * @param userId the user ID
     * @param groupId the group ID
     * @return when complete
     */
    fun insert(userId: Long, groupId: Long): Mono<Void>

    /**
     * Remove a user from a group
     * @param userId the user ID
     * @param groupId the group ID
     * @return when complete
     */
    fun delete(userId: Long, groupId: Long): Mono<Void>

    /**
     * Remove a user from all groups
     * @param userId the user ID
     * @return when complete
     */
    fun deleteUserGroupsForUser(userId: Long): Mono<Void>

    /**
     * Remove a group from all users
     * @param groupId the group ID
     * @return when complete
     */
    fun deleteUserGroupsForGroup(groupId: Long): Mono<Void>
}