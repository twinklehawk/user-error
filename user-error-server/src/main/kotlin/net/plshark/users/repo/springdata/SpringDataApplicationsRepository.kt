package net.plshark.users.repo.springdata

import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import net.plshark.users.model.Application
import net.plshark.users.model.ApplicationCreate
import net.plshark.users.repo.ApplicationsRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.stereotype.Repository

/**
 * Role repository that uses spring data and r2dbc
 */
@Repository
class SpringDataApplicationsRepository(private val client: DatabaseClient) : ApplicationsRepository {

    override suspend fun findById(id: Long): Application? {
        return client.sql("SELECT * FROM applications WHERE id = :id")
            .bind("id", id)
            .map { row -> mapRow(row) }
            .awaitOneOrNull()
    }

    override suspend fun findByName(name: String): Application? {
        return client.sql("SELECT * FROM applications WHERE name = :name")
            .bind("name", name)
            .map { row -> mapRow(row) }
            .awaitOneOrNull()
    }

    override fun getAll(limit: Int, offset: Int): Flow<Application> {
        require(limit > 0) { "limit must be positive" }
        require(offset >= 0) { "offset cannot be negative" }
        return client.sql("SELECT * FROM applications ORDER BY id LIMIT $limit OFFSET $offset")
            .map { row -> mapRow(row) }
            .all().asFlow()
    }

    override suspend fun insert(application: ApplicationCreate): Application {
        val id = client.sql("INSERT INTO applications (name) VALUES (:name) RETURNING id")
            .bind("name", application.name)
            .fetch().one()
            .map { it["id"] as Long? ?: throw IllegalStateException("No ID returned from insert") }
            .awaitSingle()
        return Application(id = id, name = application.name)
    }

    override suspend fun deleteById(id: Long) {
        client.sql("DELETE FROM applications WHERE id = :id")
            .bind("id", id)
            .await()
    }

    companion object {
        fun mapRow(row: Row): Application {
            return Application(
                id = row["id", java.lang.Long::class.java]!!.toLong(),
                name = row["name", String::class.java]!!
            )
        }
    }
}
