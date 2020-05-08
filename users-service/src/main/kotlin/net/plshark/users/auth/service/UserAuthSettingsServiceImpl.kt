package net.plshark.users.auth.service

import net.plshark.users.auth.AuthProperties
import net.plshark.users.auth.model.UserAuthSettings
import net.plshark.users.auth.repo.UserAuthSettingsRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Implementation of [UserAuthSettingsService]
 */
@Component
class UserAuthSettingsServiceImpl(
    private val settingsRepo: UserAuthSettingsRepository,
    authProperties: AuthProperties
) :
    UserAuthSettingsService {
    private val defaultAuthSettings: UserAuthSettings = UserAuthSettings(
        id = null,
        userId = null,
        authTokenExpiration = authProperties.tokenExpiration,
        refreshTokenExpiration = authProperties.tokenExpiration
    )

    override fun findByUsername(username: String): Mono<UserAuthSettings> {
        return settingsRepo.findByUsername(username)
            .defaultIfEmpty(defaultAuthSettings)
    }

    override fun getDefaultTokenExpiration(): Long {
        return defaultAuthSettings.authTokenExpiration!!
    }
}