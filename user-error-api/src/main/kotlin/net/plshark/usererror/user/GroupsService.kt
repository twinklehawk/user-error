package net.plshark.usererror.user

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for managing groups
 */
interface GroupsService {

    /**
     * Find a group by ID
     * @param id the group ID
     * @return a [Mono] emitting the matching group or empty if not found
     */
    fun findById(id: Long): Mono<Group>

    /**
     * Find a group by ID
     * @param id the group ID
     * @return a [Mono] emitting the matching group or an [net.plshark.errors.ObjectNotFoundException] if no match is
     * found
     */
    fun findRequiredById(id: Long): Mono<Group>

    /**
     * Get all groups up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return a [Flux] emitting the group
     */
    fun getGroups(maxResults: Int, offset: Long): Flux<Group>

    /**
     * Save a new group
     * @param group the group
     * @return a [Mono] emitting the saved group or a [net.plshark.errors.DuplicateException] if a group with the same
     * name already exists
     */
    fun create(group: GroupCreate): Mono<Group>

    /**
     * Delete a group by ID
     * @param groupId the group ID
     * @return a [Mono] signalling when complete
     */
    fun deleteById(groupId: Long): Mono<Void>

    /**
     * Add a role to a group
     * @param groupId the group ID
     * @param roleId the role ID
     * @return a [Mono] signalling when complete
     */
    fun addRoleToGroup(groupId: Long, roleId: Long): Mono<Void>

    /**
     * Remove a role from a group
     * @param groupId the group ID
     * @param roleId the role ID
     * @return a [Mono] signalling when complete
     */
    fun removeRoleFromGroup(groupId: Long, roleId: Long): Mono<Void>
}
