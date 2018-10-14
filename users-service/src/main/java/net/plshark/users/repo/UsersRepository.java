package net.plshark.users.repo;

import net.plshark.users.model.User;
import reactor.core.publisher.Mono;

/**
 * Repository for saving, deleting, and retrieving users
 */
public interface UsersRepository {

    /**
     * Get a user by user ID
     * @param id the user ID
     * @return the matching user
     */
    Mono<User> getForId(long id);

    /**
     * Get a user by the username
     * @param username the username
     * @return the matching user
     */
    Mono<User> getForUsername(String username);

    /**
     * Insert a new user
     * @param user the user to insert
     * @return the inserted user, will have the ID set
     */
    Mono<User> insert(User user);

    /**
     * Update an existing user's password
     * @param id the ID of the user to update
     * @param currentPassword the current password
     * @param newPassword the new password
     * @return an empty result
     */
    Mono<Void> updatePassword(long id, String currentPassword, String newPassword);

    /**
     * Delete a user by ID
     * @param userId the user ID
     * @return an empty result
     */
    Mono<Void> delete(long userId);
}
