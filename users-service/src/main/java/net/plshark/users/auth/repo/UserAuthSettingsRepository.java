package net.plshark.users.auth.repo;

import net.plshark.users.auth.model.UserAuthSettings;
import reactor.core.publisher.Mono;

/**
 * Methods for user authentication settings in the repository
 */
public interface UserAuthSettingsRepository {

    /**
     * Find settings for a user by the user's ID
     * @param userId the user's ID
     * @return the settings if found or empty if no settings exist for the user
     */
    Mono<UserAuthSettings> findByUserId(long userId);

    /**
     * Find settings for a user by the user's username
     * @param username the username
     * @return the settings if found or empty if no settings exist for the user
     */
    Mono<UserAuthSettings> findByUsername(String username);

    /**
     * Save settings for a user
     * @param userAuthSettings the settings, the user ID must not be null
     * @return the inserted settings with the ID set
     */
    Mono<UserAuthSettings> insert(UserAuthSettings userAuthSettings);
}
