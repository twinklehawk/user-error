package net.plshark.usererror.role

import kotlinx.coroutines.flow.Flow

interface UserGroupsRepository {

    /**
     * Find all the groups a user belongs to
     * @param userId the ID of the user
     * @return a [Flow] emitting the groups
     */
    fun findGroupsByUserId(userId: Long): Flow<Group>

    /**
     * Find all roles a user has through the groups a user belongs to
     * @param userId the ID of the user
     * @return a [Flow] emitting the roles
     */
    fun findGroupRolesByUserId(userId: Long): Flow<Role>

    /**
     * Add a user to a group
     * @param userId the user ID
     * @param groupId the group ID
     */
    suspend fun insert(userId: Long, groupId: Long)

    /**
     * Remove a user from a group
     * @param userId the user ID
     * @param groupId the group ID
     */
    suspend fun deleteById(userId: Long, groupId: Long)
}
