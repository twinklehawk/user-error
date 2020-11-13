package net.plshark.users.repo.springdata

import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import net.plshark.users.model.Role
import net.plshark.users.model.RoleCreate
import net.plshark.users.repo.RolesRepository
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.await
import org.springframework.data.r2dbc.core.awaitOneOrNull
import org.springframework.stereotype.Repository

/**
 * Role repository that uses spring data and r2dbc
 */
@Repository
class SpringDataRolesRepository(private val client: DatabaseClient) : RolesRepository {

    override suspend fun findById(id: Long): Role? {
        return client.execute("SELECT * FROM roles WHERE id = :id")
            .bind("id", id)
            .map { row -> mapRow(row) }
            .awaitOneOrNull()
    }

    override suspend fun findByApplicationIdAndName(applicationId: Long, name: String): Role? {
        return client.execute("SELECT * FROM roles WHERE application_id = :applicationId AND name = :name")
            .bind("applicationId", applicationId)
            .bind("name", name)
            .map { row -> mapRow(row) }
            .awaitOneOrNull()
    }

    override fun getRoles(limit: Int, offset: Int): Flow<Role> {
        require(limit >= 1) { "Max results must be greater than 0" }
        require(offset >= 0) { "Offset cannot be negative" }
        val sql = "SELECT * FROM roles ORDER BY id OFFSET $offset ROWS FETCH FIRST $limit ROWS ONLY"
        return client.execute(sql)
            .map { row -> mapRow(row) }
            .all()
            .asFlow()
    }

    override suspend fun insert(role: RoleCreate): Role {
        val id =
            client.execute("INSERT INTO roles (application_id, name) VALUES (:applicationId, :name) RETURNING id")
                .bind("applicationId", role.applicationId)
                .bind("name", role.name)
                .fetch().one()
                .map { it["id"] as Long? ?: throw IllegalStateException("No ID returned from insert") }
                .awaitSingle()
        return Role(id = id, applicationId = role.applicationId, name = role.name)
    }

    override suspend fun deleteById(id: Long) {
        return client.execute("DELETE FROM roles WHERE id = :id")
            .bind("id", id)
            .await()
    }

    suspend fun deleteAll() {
        return client.execute("DELETE FROM roles").await()
    }

    override fun findRolesByApplicationId(applicationId: Long): Flow<Role> {
        return client.execute("SELECT * FROM roles WHERE application_id = :applicationId ORDER BY id")
            .bind("applicationId", applicationId)
            .map { row -> mapRow(row) }
            .all()
            .asFlow()
    }

    override fun findRolesByApplicationId(applicationId: Long, limit: Int, offset: Int): Flow<Role> {
        require(offset >= 0) { "offset cannot be negative" }
        require(limit > 0) { "limit must be greater than 0" }
        return client.execute(
            "SELECT * FROM roles WHERE application_id = :applicationId ORDER BY id LIMIT $limit OFFSET $offset"
        )
            .bind("applicationId", applicationId)
            .map { row -> mapRow(row) }
            .all()
            .asFlow()
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
