package net.plshark.users.auth.repo.springdata

import io.r2dbc.spi.Row
import kotlinx.coroutines.reactive.awaitSingle
import net.plshark.users.auth.model.UserAuthSettings
import net.plshark.users.auth.repo.UserAuthSettingsRepository
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.awaitOneOrNull
import org.springframework.data.relational.core.query.Criteria
import org.springframework.stereotype.Repository
import java.util.Objects

@Repository
class SpringDataUserAuthSettingsRepository(private val client: DatabaseClient) : UserAuthSettingsRepository {

    override suspend fun findByUserId(userId: Long): UserAuthSettings? {
        return client.select()
            .from(UserAuthSettings::class.java)
            .matching(Criteria.where("user_id").`is`(userId))
            .fetch()
            .awaitOneOrNull()
    }

    override suspend fun findByUsername(username: String): UserAuthSettings? {
        return client.execute(
            "SELECT * FROM user_auth_settings uas, users u WHERE uas.user_id = u.id AND u.username = :username"
        )
            .bind("username", username)
            .`as`(UserAuthSettings::class.java)
            .fetch()
            .awaitOneOrNull()
    }

    override suspend fun insert(userAuthSettings: UserAuthSettings): UserAuthSettings {
        require(userAuthSettings.id == null) { "Cannot insert settings with ID already set" }
        Objects.requireNonNull(userAuthSettings.userId, "User ID cannot be null")
        return client.insert()
            .into(UserAuthSettings::class.java)
            .using(userAuthSettings)
            .map { row: Row -> row.get("id", java.lang.Long::class.java)!!.toLong() }
            .one()
            .map { id: Long -> userAuthSettings.copy(id = id) }
            .awaitSingle()
    }
}
