package net.plshark.users.repo

import kotlinx.coroutines.flow.Flow
import net.plshark.users.model.Role
import net.plshark.users.model.RoleCreate

/**
 * Repository for saving, deleting, and retrieving roles
 */
interface RolesRepository {

    /**
     * Find a role by ID
     * @param id the ID
     * @return the matching role or null if not found
     */
    suspend fun findById(id: Long): Role?

    /**
     * Find a role by name
     * @param applicationId the parent application ID
     * @param name the role name
     * @return the matching role or null if not found
     */
    suspend fun findByApplicationIdAndName(applicationId: Long, name: String): Role?

    /**
     * Find all roles up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return a [Flow] emitting the roles
     */
    fun getRoles(maxResults: Int, offset: Long): Flow<Role>

    /**
     * Find all roles belonging to an application
     * @param applicationId the application ID
     * @return a [Flow] emitting the roles
     */
    fun findRolesByApplicationId(applicationId: Long): Flow<Role>

    /**
     * Insert a new role
     * @param role the role to insert
     * @return the inserted role
     */
    suspend fun insert(role: RoleCreate): Role

    /**
     * Delete a role by ID
     * @param id the role ID
     */
    suspend fun deleteById(id: Long)
}
