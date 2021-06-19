package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.plshark.testutils.DbIntTest
import net.plshark.users.model.ApplicationCreate
import net.plshark.users.model.RoleCreate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.r2dbc.core.DatabaseClient

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
    fun `inserting a role returns the inserted role with the ID set`() = runBlocking {
        val app = appsRepo.insert(ApplicationCreate("app"))

        val inserted = repo.insert(RoleCreate(app.id, "test-role"))

        assertNotNull(inserted.id)
        assertEquals("test-role", inserted.name)
        assertEquals(app.id, inserted.applicationId)
    }

    @Test
    fun `can retrieve a previously inserted role by ID`() = runBlocking {
        val app = appsRepo.insert(ApplicationCreate("app"))
        val inserted = repo.insert(RoleCreate(app.id, "test-role"))

        val role = repo.findById(inserted.id)

        assertEquals(inserted, role)
    }

    @Test
    fun `retrieving a role by ID when no role matches returns empty`() = runBlocking {
        assertNull(repo.findById(1000))
    }

    @Test
    fun `can retrieve a previously inserted role by name`() = runBlocking {
        val app = appsRepo.insert(ApplicationCreate("app"))
        val inserted = repo.insert(RoleCreate(app.id, "test-role"))

        val role = repo.findByApplicationIdAndName(app.id, "test-role")

        assertEquals(inserted, role)
    }

    @Test
    fun `retrieving a role by name when no role matches returns empty`() = runBlocking {
        assertNull(repo.findByApplicationIdAndName(1, "test-role"))
    }

    @Test
    fun `can delete a previously inserted role by ID`() = runBlocking {
        val app = appsRepo.insert(ApplicationCreate("app"))
        val inserted = repo.insert(RoleCreate(app.id, "test-role"))

        repo.deleteById(inserted.id)
        val retrieved = repo.findById(inserted.id)

        assertNull(retrieved)
    }

    @Test
    fun `no exception is thrown when attempting to delete a role that does not exist`() = runBlocking {
        repo.deleteById(10000)
    }

    @Test
    fun `getRoles should return all results when there are less than max results`() = runBlocking {
        val app = appsRepo.insert(ApplicationCreate("app"))
        repo.deleteAll()
        repo.insert(RoleCreate(app.id, "name"))
        repo.insert(RoleCreate(app.id, "name2"))

        val roles = repo.getRoles(5, 0).toList()

        assertEquals(2, roles.size)
        assertEquals("name", roles[0].name)
        assertEquals("name2", roles[1].name)
    }

    @Test
    fun `getRoles should return up to max results when there are more results`() = runBlocking {
        val app = appsRepo.insert(ApplicationCreate("app"))
        repo.deleteAll()
        repo.insert(RoleCreate(app.id, "name"))
        repo.insert(RoleCreate(app.id, "name2"))
        repo.insert(RoleCreate(app.id, "name3"))

        val roles = repo.getRoles(2, 0).toList()

        assertEquals(2, roles.size)
        assertEquals("name", roles[0].name)
        assertEquals("name2", roles[1].name)
    }

    @Test
    fun `getRoles should start at the correct offset`() = runBlocking {
        val app = appsRepo.insert(ApplicationCreate("app"))
        repo.deleteAll()
        repo.insert(RoleCreate(app.id, "name"))
        repo.insert(RoleCreate(app.id, "name2"))
        repo.insert(RoleCreate(app.id, "name3"))

        val roles = repo.getRoles(2, 1).toList()

        assertEquals(2, roles.size)
        assertEquals("name2", roles[0].name)
        assertEquals("name3", roles[1].name)
    }

    @Test
    fun `getRolesForApplication should return all rows with a matching application ID`() = runBlocking {
        val app = appsRepo.insert(ApplicationCreate("app"))
        val app2 = appsRepo.insert(ApplicationCreate("app2"))
        repo.insert(RoleCreate(app.id, "r1"))
        repo.insert(RoleCreate(app.id, "r2"))
        repo.insert(RoleCreate(app2.id, "r3"))

        val list = repo.findRolesByApplicationId(app.id).toList()
        assertEquals(2, list.size)
        assertEquals("r1", list[0].name)
        assertEquals("r2", list[1].name)
    }

    @Test
    fun `getRolesForApplication should return up to the limit`(): Unit = runBlocking<Unit> {
        val app = appsRepo.insert(ApplicationCreate("app"))
        val app2 = appsRepo.insert(ApplicationCreate("app2"))
        repo.insert(RoleCreate(app.id, "r1"))
        repo.insert(RoleCreate(app.id, "r2"))
        repo.insert(RoleCreate(app2.id, "r3"))

        assertThat(repo.findRolesByApplicationId(app.id, 1, 0).toList())
            .hasSize(1).anyMatch { it.name == "r1" }
        assertThat(repo.findRolesByApplicationId(app.id, 1, 1).toList())
            .hasSize(1).anyMatch { it.name == "r2" }
        assertThat(repo.findRolesByApplicationId(app.id, 5, 0).toList())
            .hasSize(2).anyMatch { it.name == "r1" }.anyMatch { it.name == "r2" }
    }
}
