package net.plshark.users.repo.springdata

import io.r2dbc.spi.Row
import net.plshark.users.model.Group
import net.plshark.users.repo.GroupsRepository
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

/**
 * Groups repository using spring data
 */
@Repository
class SpringDataGroupsRepository(private val client: DatabaseClient) : GroupsRepository {

    override fun getForId(id: Long): Mono<Group> {
        return client.execute("SELECT * FROM groups WHERE id = :id")
            .bind("id", id)
            .map { row: Row -> mapRow(row) }
            .one()
    }

    override fun getForName(name: String): Mono<Group> {
        return client.execute("SELECT * FROM groups WHERE name = :name")
            .bind("name", name)
            .map { row -> mapRow(row) }
            .one()
    }

    override fun getGroups(maxResults: Int, offset: Long): Flux<Group> {
        require(maxResults >= 1) { "Max results must be greater than 0" }
        require(offset >= 0) { "Offset cannot be negative" }
        val sql = "SELECT * FROM groups ORDER BY id OFFSET $offset ROWS FETCH FIRST $maxResults ROWS ONLY"
        return client.execute(sql)
            .map { row -> mapRow(row) }
            .all()
    }

    override fun insert(group: Group): Mono<Group> {
        require(group.id == null) { "Cannot insert group with ID already set" }
        return client.execute("INSERT INTO groups (name) VALUES (:name) RETURNING id")
            .bind("name", group.name)
            .fetch().one()
            .flatMap { map ->
                Optional.ofNullable(map["id"] as Long?)
                    .map { data -> Mono.just(data) }
                    .orElse(Mono.empty())
            }
            .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
            .map { id: Long? -> group.copy(id = id) }
    }

    override fun delete(groupId: Long): Mono<Void> {
        return client.execute("DELETE FROM groups WHERE id = :id")
            .bind("id", groupId)
            .then()
    }

    companion object {
        /**
         * Map a database row to a [Group]
         * @param row the database row
         * @return the mapped group
         */
        fun mapRow(row: Row): Group {
            return Group(
                id = row["id", Long::class.java],
                name = row["name", String::class.java]!!
            )
        }
    }

}