package net.plshark.usererror.server.user.impl

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.server.testutil.DbTest
import net.plshark.usererror.user.PrivateUser
import net.plshark.usererror.user.UserCreate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.r2dbc.core.DatabaseClient

@DbTest
class UsersRepositoryImplTest {

    private lateinit var repo: UsersRepositoryImpl

    @BeforeEach
    fun setup(dbClient: DatabaseClient) {
        repo = UsersRepositoryImpl(dbClient)
    }

    @Test
    fun `inserting a user returns the inserted user with the ID set`() = runBlocking {
        val inserted = repo.insert(UserCreate("name", "pass"))

        assertNotNull(inserted.id)
        assertEquals("name", inserted.username)
    }

    @Test
    fun `can retrieve a previously inserted user by ID`() = runBlocking {
        val inserted = repo.insert(UserCreate("name", "pass"))

        val user = repo.findById(inserted.id)

        assertEquals(inserted, user)
    }

    @Test
    fun `can retrieve a previously inserted user by username`() = runBlocking {
        val inserted = repo.insert(UserCreate("name", "pass"))

        val user = repo.findByUsername("name")

        assertEquals(inserted, user)
    }

    @Test
    fun `the user password is returned when specifically fetching the password`() = runBlocking {
        val inserted = repo.insert(UserCreate("name", "pass"))

        val user = repo.findByUsernameWithPassword("name")

        assertNotNull(user)
        assertNotNull(user?.password)
        assertEquals(PrivateUser(inserted.id, inserted.username, "pass"), user)
    }

    @Test
    fun `can delete a previously inserted user by ID`() = runBlocking {
        val inserted = repo.insert(UserCreate("name", "pass"))

        repo.deleteById(inserted.id)
        val retrieved = repo.findById(inserted.id)

        assertEquals(null, retrieved)
    }

    @Test
    fun `no exception is thrown when attempting to delete a user that does not exist`() = runBlocking {
        repo.deleteById(10000)
    }

    @Test
    fun `update password should change the password if the current password is correct`() = runBlocking {
        val inserted = repo.insert(UserCreate("name", "pass"))

        repo.updatePassword(inserted.id, "pass", "new-pass")
        val user = repo.findByUsernameWithPassword("name")

        assertEquals("new-pass", user?.password)
    }

    @Test
    fun `update password should throw an EmptyResultDataAccessException if the current password is wrong`() {
        val inserted = runBlocking { repo.insert(UserCreate("name", "pass")) }

        assertThrows<EmptyResultDataAccessException> {
            runBlocking {
                repo.updatePassword(inserted.id, "wrong-pass", "new-pass")
            }
        }

        val user = runBlocking { repo.findByUsernameWithPassword("name") }
        assertEquals("pass", user?.password)
    }

    @Test
    fun `update password should throw an EmptyResultDataAccessException if no user has the ID`() {
        assertThrows<EmptyResultDataAccessException> {
            runBlocking {
                repo.updatePassword(1000, "pass", "new-pass")
            }
        }
    }

    @Test
    fun `getAll should return all results when there are less than max results`() = runBlocking {
        repo.insert(UserCreate("name", "pass"))
        repo.insert(UserCreate("name2", "pass"))

        val users = repo.getAll(5, 0).toList()

        assertEquals(3, users.size)
        // admin is inserted by the migration scripts
        assertEquals("admin", users[0].username)
        assertEquals("name", users[1].username)
        assertEquals("name2", users[2].username)
    }

    @Test
    fun `getAll should return up to max results when there are more results`() = runBlocking {
        repo.insert(UserCreate("name", "pass"))
        repo.insert(UserCreate("name2", "pass"))
        repo.insert(UserCreate("name3", "pass"))

        val users = repo.getAll(2, 0).toList()

        assertEquals(2, users.size)
        assertEquals("admin", users[0].username)
        assertEquals("name", users[1].username)
    }

    @Test
    fun `getAll should start at the correct offset`() = runBlocking {
        repo.insert(UserCreate("name", "pass"))
        repo.insert(UserCreate("name2", "pass"))
        repo.insert(UserCreate("name3", "pass"))

        val users = repo.getAll(2, 2).toList()

        assertEquals(2, users.size)
        assertEquals("name2", users[0].username)
        assertEquals("name3", users[1].username)
    }
}
