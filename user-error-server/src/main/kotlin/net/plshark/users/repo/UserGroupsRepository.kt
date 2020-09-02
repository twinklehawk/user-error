package net.plshark.users.repo

import net.plshark.users.model.Group
import net.plshark.users.model.Role
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserGroupsRepository {

    /**
     * Find all the groups a user belongs to
     * @param userId the ID of the user
     * @return a [Flux] emitting the groups
     */
    fun findGroupsByUserId(userId: Long): Flux<Group>

    /**
     * Find all roles a user has through the groups a user belongs to
     * @param userId the ID of the user
     * @return a [Flux] emitting the roles
     */
    fun findGroupRolesByUserId(userId: Long): Flux<Role>

    /**
     * Add a user to a group
     * @param userId the user ID
     * @param groupId the group ID
     * @return a [Mono] signalling when complete
     */
    fun insert(userId: Long, groupId: Long): Mono<Void>

    /**
     * Remove a user from a group
     * @param userId the user ID
     * @param groupId the group ID
     * @return a [Mono] signalling when complete
     */
    fun deleteById(userId: Long, groupId: Long): Mono<Void>
}
