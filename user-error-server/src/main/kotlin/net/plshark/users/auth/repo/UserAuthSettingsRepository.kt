package net.plshark.users.auth.repo

import net.plshark.users.auth.model.UserAuthSettings

/**
 * Methods for user authentication settings in the repository
 */
interface UserAuthSettingsRepository {

    /**
     * Find settings for a user
     * @param userId the user ID
     * @return the settings or null if not found
     */
    suspend fun findByUserId(userId: Long): UserAuthSettings?

    /**
     * Find settings for a user by the username
     * @param username the username
     * @return the settings or null if not found
     */
    suspend fun findByUsername(username: String): UserAuthSettings?

    /**
     * Save settings for a user
     * @param userAuthSettings the settings, the user ID must not be null
     * @return the inserted settings with the ID set
     */
    suspend fun insert(userAuthSettings: UserAuthSettings): UserAuthSettings
}
