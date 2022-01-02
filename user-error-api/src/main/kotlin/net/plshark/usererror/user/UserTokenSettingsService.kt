package net.plshark.usererror.user

/**
 * Service for managing user authentication settings
 */
interface UserTokenSettingsService {

    /**
     * Find the authentication settings for a user. If no settings exist for a user, default settings will be returned
     * @param username the username
     * @return the settings
     */
    suspend fun findByUsername(username: String): UserTokenSettings

    /**
     * @return the default token expiration in milliseconds
     */
    fun getDefaultTokenExpiration(): Long
}
