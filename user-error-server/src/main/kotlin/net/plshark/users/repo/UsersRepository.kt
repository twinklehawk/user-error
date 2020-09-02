package net.plshark.users.repo

import net.plshark.users.model.User
import net.plshark.users.model.UserCreate
import net.plshark.users.model.PrivateUser
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository for saving, deleting, and retrieving users
 */
interface UsersRepository {

    /**
     * Find a user by user ID
     * @param id the user ID
     * @return a [Mono] emitting the matching user or empty if not found
     */
    fun findById(id: Long): Mono<User>

    /**
     * Find a user by the username
     * @param username the username
     * @return a [Mono] emitting the matching user or empty if not found
     */
    fun findByUsername(username: String): Mono<User>

    /**
     * Find a user by the username with the user's password
     * @param username the username
     * @return a [Mono] emitting the matching user or empty if not found
     */
    fun findByUsernameWithPassword(username: String): Mono<PrivateUser>

    /**
     * Get all users up to the maximum result count
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at
     * @return a [Flux] emitting the users
     */
    fun getAll(maxResults: Int, offset: Long): Flux<User>

    /**
     * Insert a new user
     * @param user the user to insert
     * @return a [Mono] emitting the inserted user
     */
    fun insert(user: UserCreate): Mono<User>

    /**
     * Update an existing user's password
     * @param id the ID of the user to update
     * @param currentPassword the current password
     * @param newPassword the new password
     * @return a [Mono] signalling when complete
     */
    fun updatePassword(id: Long, currentPassword: String, newPassword: String): Mono<Void>

    /**
     * Delete a user by ID
     * @param userId the user ID
     * @return a [Mono] signalling when complete
     */
    fun deleteById(userId: Long): Mono<Void>
}
