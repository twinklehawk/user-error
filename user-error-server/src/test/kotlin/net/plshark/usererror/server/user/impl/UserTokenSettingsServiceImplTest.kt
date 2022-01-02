package net.plshark.usererror.server.user.impl

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.server.AuthProperties
import net.plshark.usererror.server.user.UserTokenSettingsRepository
import net.plshark.usererror.user.UserTokenSettings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserTokenSettingsServiceImplTest {

    private val userSettingsRepo = mockk<UserTokenSettingsRepository>()
    private val props = AuthProperties.forNone("issuer", 30)
    private val service = UserTokenSettingsServiceImpl(userSettingsRepo, props)

    @Test
    fun `looking up settings for a user should return the matching settings when found`() = runBlocking {
        val settings = UserTokenSettings(
            id = null,
            userId = null,
            refreshTokenEnabled = false,
            authTokenExpiration = null,
            refreshTokenExpiration = null
        )
        coEvery { userSettingsRepo.findByUsername("test-user") } returns settings

        assertEquals(settings, service.findByUsername("test-user"))
    }

    @Test
    fun `looking up settings for a user should return default settings when no match is found`() = runBlocking {
        coEvery { userSettingsRepo.findByUsername("test-user") } returns null

        assertEquals(
            UserTokenSettings(
                id = null,
                userId = null,
                refreshTokenEnabled = true,
                authTokenExpiration = 30,
                refreshTokenExpiration = 30
            ),
            service.findByUsername("test-user")
        )
    }
}
