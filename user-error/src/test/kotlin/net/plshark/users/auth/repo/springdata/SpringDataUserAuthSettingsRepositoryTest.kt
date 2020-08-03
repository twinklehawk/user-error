package net.plshark.users.auth.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import net.plshark.testutils.DbIntTest
import net.plshark.users.auth.model.UserAuthSettings
import net.plshark.users.model.UserCreate
import net.plshark.users.repo.springdata.SpringDataUsersRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier

class SpringDataUserAuthSettingsRepositoryTest : DbIntTest() {

    private lateinit var repo: SpringDataUserAuthSettingsRepository
    private lateinit var usersRepo: SpringDataUsersRepository

    @BeforeEach
    fun setup() {
        val connectionFactory = ConnectionFactories.get(DB_URL)
        val db = DatabaseClient.create(connectionFactory)
        repo = SpringDataUserAuthSettingsRepository(db)
        usersRepo = SpringDataUsersRepository(db)
    }

    @Test
    fun `inserting settings returns the inserted settings with the ID set`() {
        val user = usersRepo.insert(UserCreate(username = "test-user", password = "test-pass")).block()!!
        val inserted = repo.insert(
                UserAuthSettings(
                    id = null,
                    userId = user.id,
                    refreshTokenEnabled = false,
                    refreshTokenExpiration = null,
                    authTokenExpiration = 40
                )
            ).block()!!

        assertNotNull(inserted.id)
        assertEquals(user.id, inserted.userId)
        assertFalse(inserted.refreshTokenEnabled)
        assertEquals(40, inserted.authTokenExpiration)
        assertNull(inserted.refreshTokenExpiration)
    }

    @Test
    fun `cannot insert settings with an ID already set`() {
        assertThrows<IllegalArgumentException> {
            repo.insert(UserAuthSettings(
                id = 100,
                userId = 200,
                refreshTokenEnabled = false,
                refreshTokenExpiration = null,
                authTokenExpiration = null
            )).block()
        }
    }

    @Test
    fun `cannot insert settings without a user ID set`() {
        assertThrows<NullPointerException> {
            repo.insert(UserAuthSettings(
                id = null,
                userId = null,
                refreshTokenEnabled = false,
                refreshTokenExpiration = null,
                authTokenExpiration = null
            )).block()
        }
    }

    @Test
    fun `can retrieve previously inserted settings by user ID`() {
        val user = usersRepo.insert(UserCreate(username = "test-user", password = "test-pass")).block()!!
        val inserted = repo.insert(UserAuthSettings(
            id = null,
            userId = user.id,
            refreshTokenEnabled = true,
            refreshTokenExpiration = null,
            authTokenExpiration = null
        )).block()

        assertEquals(inserted, repo.findByUserId(user.id).block())
    }

    @Test
    fun `retrieving by user ID when no rows match returns empty`() {
        StepVerifier.create(repo.findByUserId(1000))
                .verifyComplete()
    }

    @Test
    fun `can retrieve previously inserted settings by username`() {
        val user = usersRepo.insert(UserCreate(username = "test-user", password = "test-pass")).block()!!
        val inserted = repo.insert(UserAuthSettings(
            id = null,
            userId = user.id,
            refreshTokenEnabled = true,
            refreshTokenExpiration = null,
            authTokenExpiration = null
        )).block()

        assertEquals(inserted, repo.findByUsername(user.username).block())
    }

    @Test
    fun `retrieving by username when no rows match returns empty`() {
        StepVerifier.create(repo.findByUsername("not a user"))
                .verifyComplete()
    }
}
