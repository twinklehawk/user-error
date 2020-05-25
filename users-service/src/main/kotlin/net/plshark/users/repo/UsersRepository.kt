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
     * Get a user by user ID
     * @param id the user ID
     * @return the matching user
     */
    fun getForId(id: Long): Mono<User>

    /**
     * Get a user by the username
     * @param username the username
     * @return the matching user
     */
    fun getForUsername(username: String): Mono<User>

    /**
     * Get a user by the username
     *
     ***This method returns the user's password**
     * @param username the username
     * @return the matching user
     */
    fun getForUsernameWithPassword(username: String): Mono<PrivateUser>

    /**
     * Get all users up to the maximum result count
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at
     * @return the users
     */
    fun getAll(maxResults: Int, offset: Long): Flux<User>

    /**
     * Insert a new user
     * @param user the user to insert
     * @return the inserted user, will have the ID set
     */
    fun insert(user: UserCreate): Mono<User>

    /**
     * Update an existing user's password
     * @param id the ID of the user to update
     * @param currentPassword the current password
     * @param newPassword the new password
     * @return an empty result
     */
    fun updatePassword(id: Long, currentPassword: String, newPassword: String): Mono<Void>

    /**
     * Delete a user by ID
     * @param userId the user ID
     * @return an empty result
     */
    fun delete(userId: Long): Mono<Void>
}