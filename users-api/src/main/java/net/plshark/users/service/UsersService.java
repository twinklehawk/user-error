package net.plshark.users.service;

import net.plshark.users.model.Role;
import net.plshark.users.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service for modifying users and roles
 */
public interface UsersService {

    /**
     * Retrieve a user by username
     * @param username the username
     * @return the matching user if found
     */
    Mono<User> getUserByUsername(String username);

    /**
     * Get all users up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return the users
     */
    Flux<User> getUsers(int maxResults, long offset);

    /**
     * Save a new user
     * @param user the user
     * @return the saved user
     */
    Mono<User> insertUser(User user);

    /**
     * Delete a user by ID
     * @param userId the user ID
     * @return an empty result
     */
    Mono<Void> deleteUser(long userId);

    /**
     * Delete a user
     * @param username the username
     * @return an empty result
     */
    Mono<Void> deleteUser(String username);

    /**
     * Update a user's password
     * @param userId the ID of the user
     * @param currentPassword the current password, used for verification
     * @param newPassword the new password
     * @return an empty result or ObjectNotFoundException if the user was not found
     */
    Mono<Void> updateUserPassword(long userId, String currentPassword, String newPassword);

    /**
     * Grant a role to a user
     * @param userId the ID of the user to grant the role to
     * @param roleId the ID of the role to grant
     * @return an empty result or ObjectNotFoundException if the user or role does not exist
     */
    Mono<Void> grantRoleToUser(long userId, long roleId);

    /**
     * Grant a role to a user
     * @param user the user to grant the role to
     * @param role the role to grant
     * @return an empty result or ObjectNotFoundException if the user or role does not exist
     */
    Mono<Void> grantRoleToUser(User user, Role role);

    /**
     * Remove a role from a user
     * @param userId the ID of the user to remove the role from
     * @param roleId the ID of the role to remove
     * @return an empty result or ObjectNotFoundException if the user does not exist
     */
    Mono<Void> removeRoleFromUser(long userId, long roleId);

    /**
     * Add a user to a group
     * @param userId the user ID
     * @param groupId the group ID
     * @return when complete
     */
    Mono<Void> addUserToGroup(long userId, long groupId);

    /**
     * Remove a user from a group
     * @param userId the user ID
     * @param groupId the group ID
     * @return when complete
     */
    Mono<Void> removeUserFromGroup(long userId, long groupId);
}
