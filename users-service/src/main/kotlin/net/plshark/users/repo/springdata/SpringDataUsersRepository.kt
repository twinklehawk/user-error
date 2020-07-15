package net.plshark.users.repo.springdata

import io.r2dbc.spi.Row
import net.plshark.users.model.User
import net.plshark.users.model.UserCreate
import net.plshark.users.model.PrivateUser
import net.plshark.users.repo.UsersRepository
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

/**
 * User repository that uses spring data and r2dbc
 */
@Repository
class SpringDataUsersRepository(private val client: DatabaseClient) : UsersRepository {

    override fun getForUsername(username: String): Mono<User> {
        Objects.requireNonNull(username, "username cannot be null")
        return client.execute("SELECT id, username FROM users WHERE username = :username")
            .bind("username", username)
            .map { row -> mapRow(row) }
            .one()
    }

    override fun getForUsernameWithPassword(username: String): Mono<PrivateUser> {
        Objects.requireNonNull(username, "username cannot be null")
        return client.execute("SELECT * FROM users WHERE username = :username")
            .bind("username", username)
            .map { row -> mapRowWithPassword(row) }
            .one()
    }

    override fun insert(user: UserCreate): Mono<User> {
        require(user.password.isNotEmpty()) { "Cannot insert user with blank password" }
        return client.execute("INSERT INTO users (username, password) VALUES (:username, :password) RETURNING id")
            .bind("username", user.username)
            .bind("password", user.password)
            .fetch().one()
            .flatMap { map: Map<String?, Any?> ->
                Optional.ofNullable(map["id"] as Long?)
                    .map { data: Long? -> Mono.just(data!!) }
                    .orElse(Mono.empty())
            }
            .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
            .map { id -> User(id = id, username = user.username) }
    }

    override fun delete(userId: Long): Mono<Void> {
        return client.execute("DELETE FROM users WHERE id = :id")
            .bind("id", userId)
            .then()
    }

    override fun getForId(id: Long): Mono<User> {
        return client.execute("SELECT * FROM users WHERE id = :id")
            .bind("id", id)
            .map { row -> mapRow(row) }
            .one()
    }

    override fun updatePassword(id: Long, currentPassword: String, newPassword: String): Mono<Void> {
        return client.execute("UPDATE users SET password = :newPassword WHERE id = :id AND password = :oldPassword")
            .bind("newPassword", newPassword)
            .bind("id", id)
            .bind("oldPassword", currentPassword)
            .fetch().rowsUpdated()
            .flatMap { updates: Int ->
                if (updates == 0) Mono.error {
                    EmptyResultDataAccessException("No matching user for password update", 1)
                }
                else Mono.just(updates)
            }
            .then()
    }

    override fun getAll(maxResults: Int, offset: Long): Flux<User> {
        require(maxResults >= 1) { "Max results must be greater than 0" }
        require(offset >= 0) { "Offset cannot be negative" }
        val sql =
            "SELECT * FROM users ORDER BY id OFFSET $offset ROWS FETCH FIRST $maxResults ROWS ONLY"
        return client.execute(sql)
            .map { row -> mapRow(row) }
            .all()
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
