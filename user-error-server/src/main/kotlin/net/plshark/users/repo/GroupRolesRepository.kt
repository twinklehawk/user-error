package net.plshark.users.repo

import kotlinx.coroutines.flow.Flow
import net.plshark.users.model.Role

/**
 * Repository for group-role associations
 */
interface GroupRolesRepository {

    /**
     * Find roles belonging to a group
     * @param groupId the ID of the group
     * @returns a [Flow] emitting all roles belonging to the group
     */
    fun findRolesForGroup(groupId: Long): Flow<Role>

    /**
     * Add a new group-role association
     * @param groupId the group ID
     * @param roleId the role ID
     */
    suspend fun insert(groupId: Long, roleId: Long)

    /**
     * Remove a group-role association
     * @param groupId the group ID
     * @param roleId the role ID
     */
    suspend fun deleteById(groupId: Long, roleId: Long)

    /**
     * Remove all associations for a group
     * @param groupId the group ID
     */
    suspend fun deleteByGroupId(groupId: Long)

    /**
     * Remove all associations for a role
     * @param roleId the role ID
     */
    suspend fun deleteByRoleId(roleId: Long)
}
