package net.plshark.users.service;

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
    Mono<User> get(String username);

    /**
     * Retrieve a user by username
     * @param username the username
     * @return the matching user or an ObjectNotFoundException if no matching user is found
     */
    Mono<User> getRequired(String username);

    /**
     * Get all users up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return the users
     */
    Flux<User> getUsers(int maxResults, long offset);

    /**
     * Save a new user
     * @param user the user, must have a password set
     * @return the saved useror a {@link net.plshark.errors.DuplicateException} if a user with the same username already
     * exists
     */
    Mono<User> create(User user);

    /**
     * Delete a user by ID
     * @param userId the user ID
     * @return an empty result
     */
    Mono<Void> delete(long userId);

    /**
     * Delete a user
     * @param username the username
     * @return an empty result
     */
    Mono<Void> delete(String username);

    /**
     * Update a user's password
     * @param username the ID of the user
     * @param currentPassword the current password, used for verification
     * @param newPassword the new password
     * @return an empty result or ObjectNotFoundException if the user was not found
     */
    Mono<Void> updateUserPassword(String username, String currentPassword, String newPassword);

    /**
     * Grant a role to a user
     * @param username the name of the user to grant the role to
     * @param applicationName the name of the role's application
     * @param roleName the name of the role to grant
     * @return an empty result or ObjectNotFoundException if the user or role does not exist
     */
    Mono<Void> grantRoleToUser(String username, String applicationName, String roleName);

    /**
     * Remove a role from a user
     * @param username the name of the user to remove the role from
     * @param applicationName the name of the role's application
     * @param roleName the name of the role to remove
     * @return an empty result or ObjectNotFoundException if the user does not exist
     */
    Mono<Void> removeRoleFromUser(String username, String applicationName, String roleName);

    /**
     * Add a user to a group
     * @param username the user name
     * @param groupName the group name
     * @return when complete
     */
    Mono<Void> grantGroupToUser(String username, String groupName);

    /**
     * Remove a user from a group
     * @param username the user name
     * @param groupName the group name
     * @return when complete
     */
    Mono<Void> removeGroupFromUser(String username, String groupName);
}
