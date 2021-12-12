@file:Suppress("TooManyFunctions")

package net.plshark.usererror.user

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for modifying users and roles
 */
interface UsersService {

    /**
     * Find a user by ID
     * @param id the user ID
     * @return a [Mono] emitting the matching user or empty if not found
     */
    fun findById(id: Long): Mono<User>

    /**
     * Find a user by username
     * @param username the username
     * @return a [Mono] emitting the matching user or empty if not found
     */
    fun findByUsername(username: String): Mono<User>

    /**
     * Find a user by username
     * @param username the username
     * @return a [Mono] emitting the matching user or an [net.plshark.errors.ObjectNotFoundException] if no matching
     * user is found
     */
    fun findRequiredByUsername(username: String): Mono<User>

    /**
     * Get all users up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return a [Flux] emitting the users
     */
    fun getUsers(maxResults: Int, offset: Long): Flux<User>

    /**
     * Save a new user
     * @param user the user, must have a password set
     * @return a [Mono] emitting the saved user or a [net.plshark.errors.DuplicateException] if a user with the same
     * username already exists
     */
    fun create(user: UserCreate): Mono<User>

    /**
     * Delete a user by ID
     * @param userId the user ID
     * @return a [Mono] signalling when complete
     */
    fun deleteById(userId: Long): Mono<Void>

    /**
     * Update a user's password
     * @param id the ID of the user
     * @param currentPassword the current password, used for verification
     * @param newPassword the new password
     * @return a [Mono] signalling when complete or emitting an [net.plshark.errors.ObjectNotFoundException] if the user
     * was not found
     */
    fun updateUserPassword(id: Long, currentPassword: String, newPassword: String): Mono<Void>

    /**
     * Find the roles assigned to a user
     * @param userId the user ID
     * @return a [Flux] emitting the roles assigned to the user or [net.plshark.errors.ObjectNotFoundException] if the
     * user was not found
     */
    fun getUserRoles(userId: Long): Flux<Role>

    /**
     * Update the roles assigned to a user
     * @param userId the user ID
     * @param updatedRoles the new set of roles to assign to the user
     * @return a [Flux] emitting the new roles assigned to the user or [net.plshark.errors.ObjectNotFoundException] if
     * the user was not found
     */
    fun updateUserRoles(userId: Long, updatedRoles: Set<Role>): Flux<Role>

    /**
     * Find the groups assigned to a user
     * @param userId the user ID
     * @return a [Flux] emitting the groups assigned to the user or [net.plshark.errors.ObjectNotFoundException] if the
     * user was not found
     */
    fun getUserGroups(userId: Long): Flux<Group>

    /**
     * Update the groups assigned to a user
     * @param userId the user ID
     * @param updatedGroups the new set of groups to assign to the user
     * @return a [Flux] emitting the new groups assigned to the user or [net.plshark.errors.ObjectNotFoundException] if
     * the user was not found
     */
    fun updateUserGroups(userId: Long, updatedGroups: Set<Group>): Flux<Group>
}
