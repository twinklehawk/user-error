package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import net.plshark.testutils.IntTest
import net.plshark.users.model.Application
import net.plshark.users.model.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier

class SpringDataRolesRepositoryTest {

    private lateinit var repo: SpringDataRolesRepository
    private lateinit var appsRepo: SpringDataApplicationsRepository

    @BeforeEach
    fun setup() {
        val connectionFactory = ConnectionFactories.get(IntTest.DB_URL)
        val db = DatabaseClient.create(connectionFactory)
        repo = SpringDataRolesRepository(db)
        appsRepo = SpringDataApplicationsRepository(db)
    }

    @Test
    fun `inserting a role returns the inserted role with the ID set`() {
        val app = appsRepo.insert(Application(null, "app")).block()!!

        val inserted = repo.insert(Role(null, app.id, "test-role")).block()!!

        assertNotNull(inserted.id)
        assertEquals("test-role", inserted.name)
        assertEquals(app.id, inserted.applicationId)
    }

    @Test
    fun `can retrieve a previously inserted role by ID`() {
        val app = appsRepo.insert(Application(null, "app")).block()!!
        val inserted = repo.insert(Role(null, app.id, "test-role")).block()!!

        val role = repo.get(inserted.id!!).block()

        assertEquals(inserted, role)
    }

    @Test
    fun `retrieving a role by ID when no role matches returns empty`() {
        StepVerifier.create(repo.get(1000))
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    @Test
    fun `can retrieve a previously inserted role by name`() {
        val app = appsRepo.insert(Application(null, "app")).block()!!
        val inserted = repo.insert(Role(null, app.id, "test-role")).block()!!

        val role = repo.get(app.id!!, "test-role").block()

        assertEquals(inserted, role)
    }

    @Test
    fun `retrieving a role by name when no role matches returns empty`() {
        StepVerifier.create(repo.get(1, "test-role"))
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    @Test
    fun `can delete a previously inserted role by ID`() {
        val app = appsRepo.insert(Application(null, "app")).block()!!
        val inserted = repo.insert(Role(null, app.id, "test-role")).block()!!

        repo.delete(inserted.id!!).block()
        val retrieved = repo.get(inserted.id!!).block()
        
        assertNull(retrieved)
    }

    @Test
    fun `no exception is thrown when attempting to delete a role that does not exist`() {
        repo.delete(10000).block()
    }

    @Test
    fun `getRoles should return all results when there are less than max results`() {
        val app = appsRepo.insert(Application(null, "app")).block()!!
        repo.insert(Role(null, app.id, "name"))
                .then(repo.insert(Role(null, app.id, "name2"))).block()

        val roles = repo.getRoles(5, 0).collectList().block()!!

        assertEquals(4, roles.size)
        // these are inserted by the migration scripts
        assertEquals("users-user", roles.get(0).name)
        assertEquals("users-admin", roles.get(1).name)
        assertEquals("name", roles.get(2).name)
        assertEquals("name2", roles.get(3).name)
    }

    @Test
    fun `getRoles should return up to max results when there are more results`() {
        val app = appsRepo.insert(Application(null, "app")).block()!!
        repo.insert(Role(null, app.id, "name")).block()
        repo.insert(Role(null, app.id, "name2")).block()
        repo.insert(Role(null, app.id, "name3")).block()

        val roles = repo.getRoles(2, 0).collectList().block()!!

        assertEquals(2, roles.size)
        assertEquals("users-user", roles.get(0).name)
        assertEquals("users-admin", roles.get(1).name)
    }

    @Test
    fun `getRoles should start at the correct offset`() {
        val app = appsRepo.insert(Application(null, "app")).block()!!
        repo.insert(Role(null, app.id, "name"))
                .then(repo.insert(Role(null, app.id, "name2")))
                .then(repo.insert(Role(null, app.id, "name3"))).block()

        val roles = repo.getRoles(2, 2).collectList().block()!!

        assertEquals(2, roles.size)
        assertEquals("name", roles.get(0).name)
        assertEquals("name2", roles.get(1).name)
    }

    @Test
    fun `getRolesForApplication should return all rows with a matching application ID`() {
        val app = appsRepo.insert(Application(null, "app")).block()!!
        val app2 = appsRepo.insert(Application(null, "app2")).block()!!
        repo.insert(Role(null, app.id, "r1")).block()
        repo.insert(Role(null, app.id, "r2")).block()
        repo.insert(Role(null, app2.id, "r3")).block()

        StepVerifier.create(repo.getRolesForApplication(app.id!!))
            .expectNextMatches { r -> r.name == "r1" }
            .expectNextMatches { r -> r.name == "r2" }
            .verifyComplete()
    }
}
