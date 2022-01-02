package net.plshark.usererror.server.user.impl

import net.plshark.usererror.server.AuthProperties
import net.plshark.usererror.server.user.UserTokenSettingsRepository
import net.plshark.usererror.user.UserTokenSettings
import net.plshark.usererror.user.UserTokenSettingsService
import org.springframework.stereotype.Component

/**
 * Implementation of [UserTokenSettingsService]
 */
@Component
class UserTokenSettingsServiceImpl(
    private val settingsRepo: UserTokenSettingsRepository,
    authProperties: AuthProperties
) : UserTokenSettingsService {

    private val defaultAuthSettings: UserTokenSettings =
        UserTokenSettings(
            id = null,
            userId = null,
            authTokenExpiration = authProperties.tokenExpiration,
            refreshTokenExpiration = authProperties.tokenExpiration
        )

    override suspend fun findByUsername(username: String): UserTokenSettings {
        return settingsRepo.findByUsername(username) ?: defaultAuthSettings
    }

    override fun getDefaultTokenExpiration(): Long {
        return defaultAuthSettings.authTokenExpiration!!
    }
}
