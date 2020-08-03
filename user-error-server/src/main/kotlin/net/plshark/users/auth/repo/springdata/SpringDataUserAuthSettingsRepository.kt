package net.plshark.users.auth.repo.springdata

import io.r2dbc.spi.Row
import net.plshark.users.auth.model.UserAuthSettings
import net.plshark.users.auth.repo.UserAuthSettingsRepository
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.relational.core.query.Criteria
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.Objects

@Repository
class SpringDataUserAuthSettingsRepository(private val client: DatabaseClient) : UserAuthSettingsRepository {

    override fun findByUserId(userId: Long): Mono<UserAuthSettings> {
        return client.select()
            .from(UserAuthSettings::class.java)
            .matching(Criteria.where("user_id").`is`(userId))
            .fetch()
            .one()
    }

    override fun findByUsername(username: String): Mono<UserAuthSettings> {
        return client.execute("SELECT * FROM user_auth_settings uas, users u WHERE uas.user_id = u.id AND " +
                "u.username = :username")
            .bind("username", username)
            .`as`(UserAuthSettings::class.java)
            .fetch()
            .one()
    }

    override fun insert(userAuthSettings: UserAuthSettings): Mono<UserAuthSettings> {
        require(userAuthSettings.id == null) { "Cannot insert settings with ID already set" }
        Objects.requireNonNull(userAuthSettings.userId, "User ID cannot be null")
        return client.insert()
            .into(UserAuthSettings::class.java)
            .using(userAuthSettings)
            .map { row: Row -> row.get("id", java.lang.Long::class.java)!!.toLong() }
            .one()
            .map { id: Long -> userAuthSettings.copy(id = id) }
    }
}
