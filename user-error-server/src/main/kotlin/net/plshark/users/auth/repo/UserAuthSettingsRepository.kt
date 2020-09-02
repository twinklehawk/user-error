package net.plshark.users.auth.repo

import net.plshark.users.auth.model.UserAuthSettings
import reactor.core.publisher.Mono

/**
 * Methods for user authentication settings in the repository
 */
interface UserAuthSettingsRepository {

    /**
     * Find settings for a user
     * @param userId the user ID
     * @return a [Mono] emitting the settings or empty if not found
     */
    fun findByUserId(userId: Long): Mono<UserAuthSettings>

    /**
     * Find settings for a user by the username
     * @param username the username
     * @return a [Mono] emitting the settings or empty if not found
     */
    fun findByUsername(username: String): Mono<UserAuthSettings>

    /**
     * Save settings for a user
     * @param userAuthSettings the settings, the user ID must not be null
     * @return a [Mono] emitting the inserted settings with the ID set
     */
    fun insert(userAuthSettings: UserAuthSettings): Mono<UserAuthSettings>
}
