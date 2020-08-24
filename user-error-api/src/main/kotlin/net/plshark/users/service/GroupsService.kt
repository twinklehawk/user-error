package net.plshark.users.service

import net.plshark.users.model.Group
import net.plshark.users.model.GroupCreate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for managing groups
 */
interface GroupsService {

    /**
     * Retrieve a group by ID
     * @param id the group ID
     * @return the matching group if found
     */
    fun findById(id: Long): Mono<Group>

    /**
     * Retrieve a group by ID
     * @param id the group ID
     * @return the matching group or an [net.plshark.errors.ObjectNotFoundException] if no match is found
     */
    fun findRequiredById(id: Long): Mono<Group>

    /**
     * Get all groups up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return the group
     */
    fun getGroups(maxResults: Int, offset: Long): Flux<Group>

    /**
     * Save a new group
     * @param group the group
     * @return the saved group or a [net.plshark.errors.DuplicateException] if a group with the same name already
     * exists
     */
    fun create(group: GroupCreate): Mono<Group>

    /**
     * Delete a group
     * @param groupId the group ID
     * @return when complete
     */
    fun delete(groupId: Long): Mono<Void>

    /**
     * Delete a group
     * @param name the group name
     * @return when complete
     */
    fun delete(name: String): Mono<Void>

    /**
     * Add a role to a group
     * @param groupId the group ID
     * @param roleId the role ID
     * @return when complete
     */
    fun addRoleToGroup(groupId: Long, roleId: Long): Mono<Void>

    /**
     * Remove a role from a group
     * @param groupId the group ID
     * @param roleId the role ID
     * @return when complete
     */
    fun removeRoleFromGroup(groupId: Long, roleId: Long): Mono<Void>
}
