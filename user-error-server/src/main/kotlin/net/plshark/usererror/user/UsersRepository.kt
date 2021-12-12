package net.plshark.usererror.user

import kotlinx.coroutines.flow.Flow

/**
 * Repository for saving, deleting, and retrieving users
 */
interface UsersRepository {

    /**
     * Find a user by user ID
     * @param id the user ID
     * @return the matching user or null if not found
     */
    suspend fun findById(id: Long): User?

    /**
     * Find a user by the username
     * @param username the username
     * @return the matching user or null if not found
     */
    suspend fun findByUsername(username: String): User?

    /**
     * Find a user by the username with the user's password
     * @param username the username
     * @return the matching user or null if not found
     */
    suspend fun findByUsernameWithPassword(username: String): PrivateUser?

    /**
     * Get all users up to the maximum result count
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at
     * @return a [Flow] emitting the users
     */
    fun getAll(maxResults: Int, offset: Long): Flow<User>

    /**
     * Insert a new user
     * @param user the user to insert
     * @return the inserted user
     */
    suspend fun insert(user: UserCreate): User

    /**
     * Update an existing user's password
     * @param id the ID of the user to update
     * @param currentPassword the current password
     * @param newPassword the new password
     * @throws [org.springframework.dao.EmptyResultDataAccessException] if no matching row was updated
     */
    suspend fun updatePassword(id: Long, currentPassword: String, newPassword: String)

    /**
     * Delete a user by ID
     * @param userId the user ID
     */
    suspend fun deleteById(userId: Long)
}
