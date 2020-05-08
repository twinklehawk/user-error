package net.plshark.users.repo.springdata

import io.r2dbc.spi.Row
import net.plshark.users.model.Application
import net.plshark.users.repo.ApplicationsRepository
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

/**
 * Role repository that uses spring data and r2dbc
 */
@Repository
class SpringDataApplicationsRepository(private val client: DatabaseClient) : ApplicationsRepository {

    override fun get(id: Long): Mono<Application> {
        return client.execute("SELECT * FROM applications WHERE id = :id")
            .bind("id", id)
            .map { row -> mapRow(row) }
            .one()
    }

    override fun get(name: String): Mono<Application> {
        return client.execute("SELECT * FROM applications WHERE name = :name")
            .bind("name", name)
            .map { row -> mapRow(row) }
            .one()
    }

    override fun insert(application: Application): Mono<Application> {
        require(application.id == null) { "Cannot insert application with ID already set" }
        return client.execute("INSERT INTO applications (name) VALUES (:name) RETURNING id")
            .bind("name", application.name)
            .fetch().one()
            .flatMap { map ->
                Optional.ofNullable(map["id"] as Long?)
                    .map { data -> Mono.just(data) }
                    .orElse(Mono.empty())
            }
            .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
            .map { id -> application.copy(id = id) }
    }

    override fun delete(id: Long): Mono<Void> {
        return client.execute("DELETE FROM applications WHERE id = :id")
            .bind("id", id)
            .then()
    }

    override fun delete(name: String): Mono<Void> {
        return client.execute("DELETE FROM applications WHERE name = :name")
            .bind("name", name)
            .then()
    }

    companion object {
        fun mapRow(row: Row): Application {
            return Application(
                id = row["id", java.lang.Long::class.java]?.toLong(),
                name = row["name", String::class.java]!!
            )
        }
    }

}
