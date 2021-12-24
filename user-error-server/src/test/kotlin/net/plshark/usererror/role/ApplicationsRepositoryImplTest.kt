package net.plshark.usererror.role

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.testutil.DbTest
import net.plshark.usererror.user.ApplicationCreate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.r2dbc.core.DatabaseClient

@DbTest
class ApplicationsRepositoryImplTest {

    private lateinit var repo: ApplicationsRepositoryImpl

    @BeforeEach
    fun setup(db: DatabaseClient) {
        repo = ApplicationsRepositoryImpl(db)
    }

    @Test
    fun `inserting an application returns the inserted application with the ID set`() = runBlocking {
        val inserted = repo.insert(ApplicationCreate("app"))

        assertNotNull(inserted.id)
        assertEquals("app", inserted.name)
    }

    @Test
    fun `can retrieve a previously inserted application by ID`() = runBlocking {
        val inserted = repo.insert(ApplicationCreate("test-app"))

        val app = repo.findById(inserted.id)

        assertEquals(inserted, app)
    }

    @Test
    fun `retrieving an application by ID when no application matches returns empty`() = runBlocking {
        assertNull(repo.findById(1000))
    }

    @Test
    fun `can retrieve a previously inserted application by name`() = runBlocking {
        val inserted = repo.insert(ApplicationCreate("test-app"))

        val app = repo.findByName(inserted.name)

        assertEquals(inserted, app)
    }

    @Test
    fun `retrieving an application by name when no application matches returns empty`() = runBlocking {
        assertNull(repo.findByName("app"))
    }

    @Test
    fun `can delete a previously inserted application by ID`() = runBlocking {
        val inserted = repo.insert(ApplicationCreate("test-app"))

        repo.deleteById(inserted.id)
        val retrieved = repo.findById(inserted.id)

        assertNull(retrieved)
    }

    @Test
    fun `no exception is thrown when attempting to delete an application by ID that does not exist`() = runBlocking {
        repo.deleteById(10000)
    }

    @Test
    fun `getAll should return a page of results`() = runBlocking<Unit> {
        val app1 = repo.insert(ApplicationCreate("app1"))
        val app2 = repo.insert(ApplicationCreate("app2"))

        // table already contains a user-error app
        assertThat(repo.getAll(1, 1).toList())
            .hasSize(1).contains(app1)
        assertThat(repo.getAll(1, 2).toList())
            .hasSize(1).contains(app2)
        assertThat(repo.getAll(5, 0).toList())
            .hasSize(3).contains(app1, app2)
    }
}
