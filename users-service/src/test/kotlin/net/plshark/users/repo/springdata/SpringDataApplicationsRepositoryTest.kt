package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import net.plshark.testutils.IntTest
import net.plshark.users.model.Application
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier

class SpringDataApplicationsRepositoryTest : IntTest() {

    lateinit var repo: SpringDataApplicationsRepository

    @BeforeEach
    fun setup() {
        val connectionFactory = ConnectionFactories.get(DB_URL)
        val db = DatabaseClient.create(connectionFactory)
        repo = SpringDataApplicationsRepository(db)
    }

    @Test
    fun `inserting an application returns the inserted application with the ID set`() {
        val inserted = repo.insert(Application(null, "app")).block()

        assertNotNull(inserted?.id)
        assertEquals("app", inserted?.name)
    }

    @Test
    fun `can retrieve a previously inserted application by ID`() {
        val inserted = repo.insert(Application(null, "test-app")).block()!!

        val app = repo.get(inserted.id!!).block()

        assertEquals(inserted, app)
    }

    @Test
    fun `retrieving an application by ID when no application matches returns empty`() {
        StepVerifier.create(repo.get(1000))
                .verifyComplete()
    }

    @Test
    fun `can retrieve a previously inserted application by name`() {
        val inserted = repo.insert(Application(null, "test-app")).block()!!

        val app = repo.get(inserted.name).block()

        assertEquals(inserted, app)
    }

    @Test
    fun `retrieving an application by name when no application matches returns empty`() {
        StepVerifier.create(repo.get("app"))
                .verifyComplete()
    }

    @Test
    fun `can delete a previously inserted application by ID`() {
        val inserted = repo.insert(Application(null, "test-app")).block()!!

        repo.delete(inserted.id!!).block()
        val retrieved = repo.get(inserted.id!!).block()

        assertNull(retrieved)
    }

    @Test
    fun `no exception is thrown when attempting to delete an application by ID that does not exist`() {
        repo.delete(10000).block()
    }

    @Test
    fun `can delete a previously inserted application by name`() {
        val inserted = repo.insert(Application(null, "test-app")).block()!!

        repo.delete(inserted.name).block()
        val retrieved = repo.get(inserted.id!!).block()

        assertNull(retrieved)
    }

    @Test
    fun `no exception is thrown when attempting to delete an application by name that does not exist`() {
        repo.delete("test").block()
    }
}
