package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import net.plshark.testutils.DbIntTest
import net.plshark.users.model.ApplicationCreate
import net.plshark.users.model.RoleCreate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier

class SpringDataRolesRepositoryTest : DbIntTest() {

    private lateinit var repo: SpringDataRolesRepository
    private lateinit var appsRepo: SpringDataApplicationsRepository

    @BeforeEach
    fun setup() {
        val connectionFactory = ConnectionFactories.get(DB_URL)
        val db = DatabaseClient.create(connectionFactory)
        repo = SpringDataRolesRepository(db)
        appsRepo = SpringDataApplicationsRepository(db)
    }

    @Test
    fun `inserting a role returns the inserted role with the ID set`() {
        val app = appsRepo.insert(ApplicationCreate("app")).block()!!

        val inserted = repo.insert(RoleCreate(app.id, "test-role")).block()!!

        assertNotNull(inserted.id)
        assertEquals("test-role", inserted.name)
        assertEquals(app.id, inserted.applicationId)
    }

    @Test
    fun `can retrieve a previously inserted role by ID`() {
        val app = appsRepo.insert(ApplicationCreate("app")).block()!!
        val inserted = repo.insert(RoleCreate(app.id, "test-role")).block()!!

        val role = repo.findById(inserted.id).block()

        assertEquals(inserted, role)
    }

    @Test
    fun `retrieving a role by ID when no role matches returns empty`() {
        StepVerifier.create(repo.findById(1000))
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    @Test
    fun `can retrieve a previously inserted role by name`() {
        val app = appsRepo.insert(ApplicationCreate("app")).block()!!
        val inserted = repo.insert(RoleCreate(app.id, "test-role")).block()!!

        val role = repo.findByApplicationIdAndName(app.id, "test-role").block()

        assertEquals(inserted, role)
    }

    @Test
    fun `retrieving a role by name when no role matches returns empty`() {
        StepVerifier.create(repo.findByApplicationIdAndName(1, "test-role"))
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    @Test
    fun `can delete a previously inserted role by ID`() {
        val app = appsRepo.insert(ApplicationCreate("app")).block()!!
        val inserted = repo.insert(RoleCreate(app.id, "test-role")).block()!!

        repo.deleteById(inserted.id).block()
        val retrieved = repo.findById(inserted.id).block()
        
        assertNull(retrieved)
    }

    @Test
    fun `no exception is thrown when attempting to delete a role that does not exist`() {
        repo.deleteById(10000).block()
    }

    @Test
    fun `getRoles should return all results when there are less than max results`() {
        val app = appsRepo.insert(ApplicationCreate("app")).block()!!
        repo.deleteAll().block()
        repo.insert(RoleCreate(app.id, "name"))
                .then(repo.insert(RoleCreate(app.id, "name2"))).block()

        val roles = repo.getRoles(5, 0).collectList().block()!!

        assertEquals(2, roles.size)
        assertEquals("name", roles[0].name)
        assertEquals("name2", roles[1].name)
    }

    @Test
    fun `getRoles should return up to max results when there are more results`() {
        val app = appsRepo.insert(ApplicationCreate("app")).block()!!
        repo.deleteAll().block()
        repo.insert(RoleCreate(app.id, "name")).block()
        repo.insert(RoleCreate(app.id, "name2")).block()
        repo.insert(RoleCreate(app.id, "name3")).block()

        val roles = repo.getRoles(2, 0).collectList().block()!!

        assertEquals(2, roles.size)
        assertEquals("name", roles[0].name)
        assertEquals("name2", roles[1].name)
    }

    @Test
    fun `getRoles should start at the correct offset`() {
        val app = appsRepo.insert(ApplicationCreate("app")).block()!!
        repo.deleteAll().block()
        repo.insert(RoleCreate(app.id, "name"))
                .then(repo.insert(RoleCreate(app.id, "name2")))
                .then(repo.insert(RoleCreate(app.id, "name3"))).block()

        val roles = repo.getRoles(2, 1).collectList().block()!!

        assertEquals(2, roles.size)
        assertEquals("name2", roles[0].name)
        assertEquals("name3", roles[1].name)
    }

    @Test
    fun `getRolesForApplication should return all rows with a matching application ID`() {
        val app = appsRepo.insert(ApplicationCreate("app")).block()!!
        val app2 = appsRepo.insert(ApplicationCreate("app2")).block()!!
        repo.insert(RoleCreate(app.id, "r1")).block()
        repo.insert(RoleCreate(app.id, "r2")).block()
        repo.insert(RoleCreate(app2.id, "r3")).block()

        StepVerifier.create(repo.findRolesByApplicationId(app.id))
            .expectNextMatches { r -> r.name == "r1" }
            .expectNextMatches { r -> r.name == "r2" }
            .verifyComplete()
    }
}
