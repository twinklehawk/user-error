package net.plshark.users.auth.service

import io.mockk.every
import io.mockk.mockk
import net.plshark.users.auth.AuthProperties
import net.plshark.users.auth.model.UserAuthSettings
import net.plshark.users.auth.repo.UserAuthSettingsRepository
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class UserAuthSettingsServiceImplTest {

    private val userSettingsRepo = mockk<UserAuthSettingsRepository>()
    private val props = AuthProperties.forNone("issuer", 30)
    private val service = UserAuthSettingsServiceImpl(userSettingsRepo, props)

    @Test
    fun `looking up settings for a user should return the matching settings when found`() {
        val settings = UserAuthSettings(id = null, userId = null, refreshTokenEnabled = false, authTokenExpiration = null, refreshTokenExpiration = null)
        every { userSettingsRepo.findByUsername("test-user") } returns Mono.just(settings)

        StepVerifier.create(service.findByUsername("test-user"))
                .expectNext(settings)
                .verifyComplete()
    }

    @Test
    fun `looking up settings for a user should return default settings when no match is found`() {
        every { userSettingsRepo.findByUsername("test-user") } returns Mono.empty()

        StepVerifier.create(service.findByUsername("test-user"))
                .expectNext(UserAuthSettings(id = null, userId = null, refreshTokenEnabled = true, authTokenExpiration = 30, refreshTokenExpiration = 30))
                .verifyComplete()
    }
}
