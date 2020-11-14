package net.plshark.users.repo

import kotlinx.coroutines.flow.Flow
import net.plshark.users.model.Group
import net.plshark.users.model.GroupCreate

/**
 * Repository for groups
 */
interface GroupsRepository {

    /**
     * Find a group by ID
     * @param id the group ID
     * @return the matching group or null if not found
     */
    suspend fun findById(id: Long): Group?

    /**
     * Find a group by name
     * @param name the group name
     * @return the matching group or null if not found
     */
    suspend fun findByName(name: String): Group?

    /**
     * Find all groups up to a maximum number of results
     * @param limit the max results to return
     * @param offset the offset to start at
     * @return a [Flow] emitting the groups
     */
    fun getGroups(limit: Int, offset: Int): Flow<Group>

    /**
     * Save a new group
     * @param group the group to save
     * @return the saved group
     */
    suspend fun insert(group: GroupCreate): Group

    /**
     * Delete a group by ID
     * @param groupId the group ID
     */
    suspend fun deleteById(groupId: Long)
}
