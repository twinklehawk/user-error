package net.plshark.users.auth.service

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.plshark.users.auth.AuthProperties
import net.plshark.users.auth.model.UserAuthSettings
import net.plshark.users.auth.repo.UserAuthSettingsRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserAuthSettingsServiceImplTest {

    private val userSettingsRepo = mockk<UserAuthSettingsRepository>()
    private val props = AuthProperties.forNone("issuer", 30)
    private val service = UserAuthSettingsServiceImpl(userSettingsRepo, props)

    @Test
    fun `looking up settings for a user should return the matching settings when found`() = runBlocking {
        val settings = UserAuthSettings(
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
            UserAuthSettings(
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
