package net.plshark.users.auth.repo

import net.plshark.users.auth.model.UserAuthSettings
import reactor.core.publisher.Mono

/**
 * Methods for user authentication settings in the repository
 */
interface UserAuthSettingsRepository {
    /**
     * Find settings for a user by the user's ID
     * @param userId the user's ID
     * @return the settings if found or empty if no settings exist for the user
     */
    fun findByUserId(userId: Long): Mono<UserAuthSettings>

    /**
     * Find settings for a user by the user's username
     * @param username the username
     * @return the settings if found or empty if no settings exist for the user
     */
    fun findByUsername(username: String): Mono<UserAuthSettings>

    /**
     * Save settings for a user
     * @param userAuthSettings the settings, the user ID must not be null
     * @return the inserted settings with the ID set
     */
    fun insert(userAuthSettings: UserAuthSettings): Mono<UserAuthSettings>
}
