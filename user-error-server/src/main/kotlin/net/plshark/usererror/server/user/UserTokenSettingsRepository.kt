package net.plshark.usererror.server.user

import net.plshark.usererror.user.UserTokenSettings

/**
 * Methods for user authentication settings in the repository
 */
interface UserTokenSettingsRepository {

    /**
     * Find settings for a user
     * @param userId the user ID
     * @return the settings or null if not found
     */
    suspend fun findByUserId(userId: Long): UserTokenSettings?

    /**
     * Find settings for a user by the username
     * @param username the username
     * @return the settings or null if not found
     */
    suspend fun findByUsername(username: String): UserTokenSettings?

    /**
     * Save settings for a user
     * @param userTokenSettings the settings, the user ID must not be null
     * @return the inserted settings with the ID set
     */
    suspend fun insert(userTokenSettings: UserTokenSettings): UserTokenSettings
}
