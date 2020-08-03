package net.plshark.users.auth.service

import net.plshark.users.auth.model.UserAuthSettings
import reactor.core.publisher.Mono

/**
 * Service for managing user authentication settings
 */
interface UserAuthSettingsService {

    /**
     * Find the authentication settings for a user. If no settings exist for a user, default settings will be returned
     * @param username the username
     * @return the settings, never empty
     */
    fun findByUsername(username: String): Mono<UserAuthSettings>

    /**
     * @return the default token expiration in milliseconds
     */
    fun getDefaultTokenExpiration(): Long
}
