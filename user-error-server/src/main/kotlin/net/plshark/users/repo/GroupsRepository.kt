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
     * Find a group by ID
     * @param id the group ID
     * @return a [Mono] emitting the matching group or empty if not found
     */
    fun findById(id: Long): Mono<Group>

    /**
     * Find a group by name
     * @param name the group name
     * @return a [Mono] emitting the matching group or empty if not found
     */
    fun findByName(name: String): Mono<Group>

    /**
     * Find all groups up to a maximum number of results
     * @param maxResults the max results to return
     * @param offset the offset to start at
     * @return a [Flux] emitting the groups
     */
    fun getGroups(maxResults: Int, offset: Long): Flux<Group>

    /**
     * Save a new group
     * @param group the group to save
     * @return a [Mono] emitting the saved group
     */
    fun insert(group: GroupCreate): Mono<Group>

    /**
     * Delete a group by ID
     * @param groupId the group ID
     * @return a [Mono] signalling when complete
     */
    fun deleteById(groupId: Long): Mono<Void>
}
