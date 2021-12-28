package net.plshark.usererror.role.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import net.plshark.usererror.role.Role
import net.plshark.usererror.role.RolesRepositoryImpl
import net.plshark.usererror.role.UserRolesRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository

/**
 * User roles repository that uses spring data and r2dbc
 */
@Repository
class UserRolesRepositoryImpl(private val client: DatabaseClient) : UserRolesRepository {

    override fun findRolesByUserId(userId: Long): Flow<Role> {
        val sql = "SELECT r.* FROM roles r INNER JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = :id"
        return client.sql(sql)
            .bind("id", userId)
            .map { row -> RolesRepositoryImpl.mapRow(row) }
            .all()
            .asFlow()
    }

    override suspend fun insert(userId: Long, roleId: Long) {
        return client.sql("INSERT INTO user_roles (user_id, role_id) values (:userId, :roleId)")
            .bind("userId", userId)
            .bind("roleId", roleId)
            .await()
    }

    override suspend fun deleteById(userId: Long, roleId: Long) {
        return client.sql("DELETE FROM user_roles WHERE user_id = :userId AND role_id = :roleId")
            .bind("userId", userId)
            .bind("roleId", roleId)
            .await()
    }
}
