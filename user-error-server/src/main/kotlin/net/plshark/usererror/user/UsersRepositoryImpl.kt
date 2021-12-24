package net.plshark.usererror.user

import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.stereotype.Repository

/**
 * User repository that uses spring data and r2dbc
 */
@Repository
class UsersRepositoryImpl(private val client: DatabaseClient) : UsersRepository {

    override suspend fun findByUsername(username: String): User? {
        return client.sql("SELECT id, username FROM users WHERE username = :username")
            .bind("username", username)
            .map { row -> mapRow(row) }
            .awaitOneOrNull()
    }

    override suspend fun findById(id: Long): User? {
        return client.sql("SELECT * FROM users WHERE id = :id")
            .bind("id", id)
            .map { row -> mapRow(row) }
            .awaitOneOrNull()
    }

    override suspend fun findByUsernameWithPassword(username: String): PrivateUser? {
        return client.sql("SELECT * FROM users WHERE username = :username")
            .bind("username", username)
            .map { row -> mapRowWithPassword(row) }
            .awaitOneOrNull()
    }

    override suspend fun insert(user: UserCreate): User {
        require(user.password.isNotEmpty()) { "Cannot insert user with blank password" }
        val id = client.sql("INSERT INTO users (username, password) VALUES (:username, :password) RETURNING id")
            .bind("username", user.username)
            .bind("password", user.password)
            .fetch().one()
            .map { it["id"] as Long? ?: throw IllegalStateException("No ID returned from insert") }
            .awaitSingle()
        return User(id = id, username = user.username)
    }

    override suspend fun deleteById(userId: Long) {
        return client.sql("DELETE FROM users WHERE id = :id")
            .bind("id", userId)
            .await()
    }

    override suspend fun updatePassword(id: Long, currentPassword: String, newPassword: String) {
        val updates = client.sql(
            "UPDATE users SET password = :newPassword WHERE id = :id AND password = :oldPassword"
        )
            .bind("newPassword", newPassword)
            .bind("id", id)
            .bind("oldPassword", currentPassword)
            .fetch().rowsUpdated()
            .awaitSingle()
        if (updates == 0) throw EmptyResultDataAccessException("No matching user for password update", 1)
    }

    override fun getAll(maxResults: Int, offset: Long): Flow<User> {
        require(maxResults >= 1) { "Max results must be greater than 0" }
        require(offset >= 0) { "Offset cannot be negative" }
        val sql =
            "SELECT * FROM users ORDER BY id OFFSET $offset ROWS FETCH FIRST $maxResults ROWS ONLY"
        return client.sql(sql)
            .map { row -> mapRow(row) }
            .all()
            .asFlow()
    }

    companion object {
        fun mapRow(row: Row): User {
            return User(
                id = row["id", java.lang.Long::class.java]!!.toLong(),
                username = row["username", String::class.java]!!
            )
        }

        private fun mapRowWithPassword(row: Row): PrivateUser {
            return PrivateUser(
                id = row["id", java.lang.Long::class.java]!!.toLong(),
                username = row["username", String::class.java]!!,
                password = row["password", String::class.java]!!
            )
        }
    }
}
