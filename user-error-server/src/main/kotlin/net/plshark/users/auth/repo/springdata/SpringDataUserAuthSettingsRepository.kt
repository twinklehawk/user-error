package net.plshark.users.auth.repo.springdata

import io.r2dbc.spi.Row
import kotlinx.coroutines.reactive.awaitSingle
import net.plshark.users.auth.model.UserAuthSettings
import net.plshark.users.auth.repo.UserAuthSettingsRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.stereotype.Repository
import java.util.Objects

@Repository
class SpringDataUserAuthSettingsRepository(private val client: DatabaseClient) : UserAuthSettingsRepository {

    override suspend fun findByUserId(userId: Long): UserAuthSettings? {
        return client.sql("SELECT * FROM user_auth_settings WHERE user_id = :userId")
            .bind("userId", userId)
            .map { row -> mapRow(row) }
            .awaitOneOrNull()
    }

    override suspend fun findByUsername(username: String): UserAuthSettings? {
        val sql = "SELECT uas.* FROM user_auth_settings uas, users u WHERE uas.user_id = u.id AND " +
                "u.username = :username"
        return client.sql(sql)
            .bind("username", username)
            .map { row -> mapRow(row) }
            .awaitOneOrNull()
    }

    override suspend fun insert(userAuthSettings: UserAuthSettings): UserAuthSettings {
        require(userAuthSettings.id == null) { "Cannot insert settings with ID already set" }
        Objects.requireNonNull(userAuthSettings.userId, "User ID cannot be null")
        val sql = "INSERT INTO user_auth_settings (user_id, refresh_token_enabled, auth_token_expiration, " +
                "refresh_token_expiration) VALUES (:userId, :refreshTokenEnabled, :authTokenExpiration, " +
                ":refreshTokenExpiration) RETURNING id"
        var spec = client.sql(sql)
            .bind("userId", userAuthSettings.userId!!)
            .bind("refreshTokenEnabled", userAuthSettings.refreshTokenEnabled)
        spec = when (val l = userAuthSettings.authTokenExpiration) {
            null -> spec.bindNull("authTokenExpiration", java.lang.Long::class.java)
            else -> spec.bind("authTokenExpiration", l)
        }
        spec = when (val l = userAuthSettings.refreshTokenExpiration) {
            null -> spec.bindNull("refreshTokenExpiration", java.lang.Long::class.java)
            else -> spec.bind("refreshTokenExpiration", l)
        }

        return spec
            .map { row: Row -> row.get("id", java.lang.Long::class.java)!!.toLong() }
            .one()
            .map { id: Long -> userAuthSettings.copy(id = id) }
            .awaitSingle()
    }

    companion object {
        fun mapRow(row: Row): UserAuthSettings {
            return UserAuthSettings(
                id = row["id", java.lang.Long::class.java]!!.toLong(),
                userId = row["user_id", java.lang.Long::class.java]!!.toLong(),
                refreshTokenEnabled = row["refresh_token_enabled", java.lang.Boolean::class.java]!!.booleanValue(),
                authTokenExpiration = row["auth_token_expiration", java.lang.Long::class.java]?.toLong(),
                refreshTokenExpiration = row["refresh_token_expiration", java.lang.Long::class.java]?.toLong()
            )
        }
    }
}
