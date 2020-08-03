package net.plshark.users.repo

import net.plshark.users.model.Group
import net.plshark.users.model.GroupCreate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository for groups
 */
interface GroupsRepository {
    /**
     * Get a group by ID
     * @param id the group ID
     * @return the matching group if found
     */
    fun getForId(id: Long): Mono<Group>

    /**
     * Get a group by name
     * @param name the group name
     * @return the matching group if found
     */
    fun getForName(name: String): Mono<Group>

    /**
     * Get all groups up to a maximum number of results
     * @param maxResults the max results to return
     * @param offset the offset to start at
     * @return the groups
     */
    fun getGroups(maxResults: Int, offset: Long): Flux<Group>

    /**
     * Save a new group
     * @param group the group to save
     * @return the saved group
     */
    fun insert(group: GroupCreate): Mono<Group>

    /**
     * Delete a group by ID
     * @param groupId the group ID
     * @return when complete
     */
    fun delete(groupId: Long): Mono<Void>
}
