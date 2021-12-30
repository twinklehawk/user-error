package net.plshark.usererror.server.role.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import net.plshark.usererror.role.Role
import net.plshark.usererror.server.role.GroupRolesRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository

@Repository
class GroupRolesRepositoryImpl(private val client: DatabaseClient) : GroupRolesRepository {

    override fun findRolesForGroup(groupId: Long): Flow<Role> {
        val sql = "SELECT r.* FROM roles r INNER JOIN group_roles ur ON r.id = ur.role_id WHERE ur.group_id = :id"
        return client.sql(sql)
            .bind("id", groupId)
            .map { row -> RolesRepositoryImpl.mapRow(row) }
            .all()
            .asFlow()
    }

    override suspend fun insert(groupId: Long, roleId: Long) {
        return client.sql("INSERT INTO group_roles (group_id, role_id) values (:groupId, :roleId)")
            .bind("groupId", groupId)
            .bind("roleId", roleId)
            .await()
    }

    override suspend fun deleteById(groupId: Long, roleId: Long) {
        return client.sql("DELETE FROM group_roles WHERE group_id = :groupId AND role_id = :roleId")
            .bind("groupId", groupId)
            .bind("roleId", roleId)
            .await()
    }

    override suspend fun deleteByGroupId(groupId: Long) {
        return client.sql("DELETE FROM group_roles WHERE group_id = :groupId")
            .bind("groupId", groupId)
            .await()
    }

    override suspend fun deleteByRoleId(roleId: Long) {
        return client.sql("DELETE FROM group_roles WHERE role_id = :roleId")
            .bind("roleId", roleId)
            .await()
    }
}
