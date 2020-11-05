package net.plshark.users.repo

import kotlinx.coroutines.flow.Flow
import net.plshark.users.model.Role

/**
 * Repository for adding and removing roles for users
 */
interface UserRolesRepository {

    /**
     * Find all the roles for a user
     * @param userId the user ID
     * @return a [Flow] emitting the roles for the user
     */
    fun findRolesByUserId(userId: Long): Flow<Role>

    /**
     * Grant a role to a user
     * @param userId the ID of the user to grant the role to
     * @param roleId the ID of the role to grant
     */
    suspend fun insert(userId: Long, roleId: Long)

    /**
     * Remove a role from a user
     * @param userId the ID of the user to remove the role from
     * @param roleId the ID of the role to remove
     */
    suspend fun deleteById(userId: Long, roleId: Long)
}
