@file:Suppress("TooManyFunctions")
package net.plshark.users.service

import net.plshark.users.model.User
import net.plshark.users.model.UserCreate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for modifying users and roles
 */
interface UsersService {

    /**
     * Retrieve a user by ID
     * @param id the user ID
     * @return a [Mono] emitting the matching user or empty if not found
     */
    fun findById(id: Long): Mono<User>

    /**
     * Retrieve a user by username
     * @param username the username
     * @return the matching user if found
     */
    fun findByUsername(username: String): Mono<User>

    /**
     * Retrieve a user by username
     * @param username the username
     * @return the matching user or an ObjectNotFoundException if no matching user is found
     */
    fun findRequiredByUsername(username: String): Mono<User>

    /**
     * Get all users up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return the users
     */
    fun getUsers(maxResults: Int, offset: Long): Flux<User>

    /**
     * Save a new user
     * @param user the user, must have a password set
     * @return the saved useror a [net.plshark.errors.DuplicateException] if a user with the same username already
     * exists
     */
    fun create(user: UserCreate): Mono<User>

    /**
     * Delete a user by ID
     * @param userId the user ID
     * @return an empty result
     */
    fun delete(userId: Long): Mono<Void>

    /**
     * Update a user's password
     * @param id the ID of the user
     * @param currentPassword the current password, used for verification
     * @param newPassword the new password
     * @return an empty result or ObjectNotFoundException if the user was not found
     */
    fun updateUserPassword(id: Long, currentPassword: String, newPassword: String): Mono<Void>

    /**
     * Grant a role to a user
     * @param id the ID of the user
     * @param applicationId the ID of the role's application
     * @param roleId the ID of the role to grant
     * @return an empty result or ObjectNotFoundException if the user or role does not exist
     */
    fun grantRoleToUser(id: Long, applicationId: Long, roleId: Long): Mono<Void>

    /**
     * Remove a role from a user
     * @param id the ID of the user
     * @param applicationId the ID of the role's application
     * @param roleId the ID of the role to remove
     * @return an empty result or ObjectNotFoundException if the user does not exist
     */
    fun removeRoleFromUser(id: Long, applicationId: Long, roleId: Long): Mono<Void>

    /**
     * Add a user to a group
     * @param id the ID of the user
     * @param groupId the group ID
     * @return when complete
     */
    fun grantGroupToUser(id: Long, groupId: Long): Mono<Void>

    /**
     * Remove a user from a group
     * @param id the ID of the user
     * @param groupId the group ID
     * @return when complete
     */
    fun removeGroupFromUser(id: Long, groupId: Long): Mono<Void>
}
