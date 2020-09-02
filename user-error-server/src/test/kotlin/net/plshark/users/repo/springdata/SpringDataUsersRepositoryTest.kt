package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import net.plshark.testutils.DbIntTest
import net.plshark.users.model.UserCreate
import net.plshark.users.model.PrivateUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier

class SpringDataUsersRepositoryTest : DbIntTest() {

    private lateinit var repo: SpringDataUsersRepository

    @BeforeEach
    fun setup() {
        val connectionFactory = ConnectionFactories.get(DB_URL)
        val dbClient = DatabaseClient.create(connectionFactory)
        repo = SpringDataUsersRepository(dbClient)
    }

    @Test
    fun `inserting a user returns the inserted user with the ID set`() {
        val inserted = repo.insert(UserCreate("name", "pass")).block()!!

        assertNotNull(inserted.id)
        assertEquals("name", inserted.username)
    }

    @Test
    fun `can retrieve a previously inserted user by ID`() {
        val inserted = repo.insert(UserCreate("name", "pass")).block()!!

        val user = repo.findById(inserted.id).block()!!

        assertEquals(inserted, user)
    }

    @Test
    fun `can retrieve a previously inserted user by username`() {
        val inserted = repo.insert(UserCreate("name", "pass")).block()!!

        val user = repo.findByUsername("name").block()!!

        assertEquals(inserted, user)
    }

    @Test
    fun `the user password is returned when specifically fetching the password`() {
        val inserted = repo.insert(UserCreate("name", "pass")).block()!!

        val user = repo.findByUsernameWithPassword("name").block()!!

        assertNotNull(user.password)
        assertEquals(PrivateUser(inserted.id, inserted.username, "pass"), user)
    }

    @Test
    fun `can delete a previously inserted user by ID`() {
        val inserted = repo.insert(UserCreate("name", "pass")).block()!!

        repo.deleteById(inserted.id).block()
        val retrieved = repo.findById(inserted.id).block()

        assertEquals(null, retrieved)
    }

    @Test
    fun `no exception is thrown when attempting to delete a user that does not exist`() {
        repo.deleteById(10000).block()
    }

    @Test
    fun `update password should change the password if the current password is correct`() {
        val inserted = repo.insert(UserCreate("name", "pass")).block()!!

        repo.updatePassword(inserted.id, "pass", "new-pass").block()
        val user = repo.findByUsernameWithPassword("name").block()!!

        assertEquals("new-pass", user.password)
    }

    @Test
    fun `update password should throw an EmptyResultDataAccessException if the current password is wrong`() {
        val inserted = repo.insert(UserCreate("name", "pass")).block()!!

        StepVerifier.create(repo.updatePassword(inserted.id, "wrong-pass", "new-pass"))
                .expectError(EmptyResultDataAccessException::class.java)
                .verify()
        val user = repo.findByUsernameWithPassword("name").block()!!

        assertEquals("pass", user.password)
    }

    @Test
    fun `update password should throw an EmptyResultDataAccessException if no user has the ID`() {
        StepVerifier.create(repo.updatePassword(1000, "pass", "new-pass"))
                .expectError(EmptyResultDataAccessException::class.java)
                .verify()
    }

    @Test
    fun `getAll should return all results when there are less than max results`() {
        repo.insert(UserCreate("name", "pass")).block()
        repo.insert(UserCreate("name2", "pass")).block()

        val users = repo.getAll(5, 0).collectList().block()!!

        assertEquals(3, users.size)
        // admin is inserted by the migration scripts
        assertEquals("admin", users[0].username)
        assertEquals("name", users[1].username)
        assertEquals("name2", users[2].username)
    }

    @Test
    fun `getAll should return up to max results when there are more results`() {
        repo.insert(UserCreate("name", "pass")).block()
        repo.insert(UserCreate("name2", "pass")).block()
        repo.insert(UserCreate("name3", "pass")).block()

        val users = repo.getAll(2, 0).collectList().block()!!

        assertEquals(2, users.size)
        assertEquals("admin", users[0].username)
        assertEquals("name", users[1].username)
    }

    @Test
    fun `getAll should start at the correct offset`() {
        repo.insert(UserCreate("name", "pass")).block()
        repo.insert(UserCreate("name2", "pass")).block()
        repo.insert(UserCreate("name3", "pass")).block()

        val users = repo.getAll(2, 2).collectList().block()!!

        assertEquals(2, users.size)
        assertEquals("name2", users[0].username)
        assertEquals("name3", users[1].username)
    }
}