package net.plshark.users.repo.springdata

import io.r2dbc.spi.Row
import net.plshark.users.model.Role
import net.plshark.users.model.RoleCreate
import net.plshark.users.repo.RolesRepository
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.Optional

/**
 * Role repository that uses spring data and r2dbc
 */
@Repository
class SpringDataRolesRepository(private val client: DatabaseClient) : RolesRepository {

    override fun get(id: Long): Mono<Role> {
        return client.execute("SELECT * FROM roles WHERE id = :id")
            .bind("id", id)
            .map { row -> mapRow(row) }
            .one()
    }

    override fun get(applicationId: Long, name: String): Mono<Role> {
        return client.execute("SELECT * FROM roles WHERE application_id = :applicationId AND name = :name")
            .bind("applicationId", applicationId)
            .bind("name", name)
            .map { row -> mapRow(row) }
            .one()
    }

    override fun getRoles(maxResults: Int, offset: Long): Flux<Role> {
        require(maxResults >= 1) { "Max results must be greater than 0" }
        require(offset >= 0) { "Offset cannot be negative" }
        val sql = "SELECT * FROM roles ORDER BY id OFFSET $offset ROWS FETCH FIRST $maxResults ROWS ONLY"
        return client.execute(sql)
            .map { row -> mapRow(row) }
            .all()
    }

    override fun insert(role: RoleCreate): Mono<Role> {
        return client.execute("INSERT INTO roles (application_id, name) VALUES (:applicationId, :name) RETURNING id")
            .bind("applicationId", role.applicationId)
            .bind("name", role.name)
            .fetch().one()
            .flatMap { map ->
                Optional.ofNullable(map["id"] as Long?)
                    .map { data -> Mono.just(data) }
                    .orElse(Mono.empty())
            }
            .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
            .map { id -> Role(id = id, applicationId = role.applicationId, name = role.name) }
    }

    override fun delete(id: Long): Mono<Void> {
        return client.execute("DELETE FROM roles WHERE id = :id")
            .bind("id", id)
            .then()
    }

    fun deleteAll(): Mono<Void> {
        return client.execute("DELETE FROM roles")
            .then()
    }

    override fun getRolesForApplication(applicationId: Long): Flux<Role> {
        return client.execute("SELECT * FROM roles WHERE application_id = :applicationId ORDER BY id")
            .bind("applicationId", applicationId)
            .map { row -> mapRow(row) }
            .all()
    }

    companion object {
        fun mapRow(row: Row): Role {
            return Role(
                id = row["id", java.lang.Long::class.java]!!.toLong(),
                applicationId = row["application_id", java.lang.Long::class.java]!!.toLong(),
                name = row["name", String::class.java]!!
            )
        }
    }
}
