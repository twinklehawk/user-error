package net.plshark.users.auth.service

import net.plshark.users.auth.AuthProperties
import net.plshark.users.auth.model.UserAuthSettings
import net.plshark.users.auth.repo.UserAuthSettingsRepository
import org.springframework.stereotype.Component

/**
 * Implementation of [UserAuthSettingsService]
 */
@Component
class UserAuthSettingsServiceImpl(
    private val settingsRepo: UserAuthSettingsRepository,
    authProperties: AuthProperties
) : UserAuthSettingsService {

    private val defaultAuthSettings: UserAuthSettings = UserAuthSettings(
        id = null,
        userId = null,
        authTokenExpiration = authProperties.tokenExpiration,
        refreshTokenExpiration = authProperties.tokenExpiration
    )

    override suspend fun findByUsername(username: String): UserAuthSettings {
        return settingsRepo.findByUsername(username) ?: defaultAuthSettings
    }

    override fun getDefaultTokenExpiration(): Long {
        return defaultAuthSettings.authTokenExpiration!!
    }
}
