package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import net.plshark.testutils.DbIntTest
import net.plshark.users.model.ApplicationCreate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier

class SpringDataApplicationsRepositoryTest : DbIntTest() {

    lateinit var repo: SpringDataApplicationsRepository

    @BeforeEach
    fun setup() {
        val connectionFactory = ConnectionFactories.get(DB_URL)
        val db = DatabaseClient.create(connectionFactory)
        repo = SpringDataApplicationsRepository(db)
    }

    @Test
    fun `inserting an application returns the inserted application with the ID set`() {
        val inserted = repo.insert(ApplicationCreate("app")).block()

        assertNotNull(inserted?.id)
        assertEquals("app", inserted?.name)
    }

    @Test
    fun `can retrieve a previously inserted application by ID`() {
        val inserted = repo.insert(ApplicationCreate("test-app")).block()!!

        val app = repo.findById(inserted.id).block()

        assertEquals(inserted, app)
    }

    @Test
    fun `retrieving an application by ID when no application matches returns empty`() {
        StepVerifier.create(repo.findById(1000))
                .verifyComplete()
    }

    @Test
    fun `can retrieve a previously inserted application by name`() {
        val inserted = repo.insert(ApplicationCreate("test-app")).block()!!

        val app = repo.findByName(inserted.name).block()

        assertEquals(inserted, app)
    }

    @Test
    fun `retrieving an application by name when no application matches returns empty`() {
        StepVerifier.create(repo.findByName("app"))
                .verifyComplete()
    }

    @Test
    fun `can delete a previously inserted application by ID`() {
        val inserted = repo.insert(ApplicationCreate("test-app")).block()!!

        repo.deleteById(inserted.id).block()
        val retrieved = repo.findById(inserted.id).block()

        assertNull(retrieved)
    }

    @Test
    fun `no exception is thrown when attempting to delete an application by ID that does not exist`() {
        repo.deleteById(10000).block()
    }
}
