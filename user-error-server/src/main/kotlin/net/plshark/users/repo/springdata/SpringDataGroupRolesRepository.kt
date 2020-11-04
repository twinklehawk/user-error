package net.plshark.users.repo.springdata

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import net.plshark.users.model.Role
import net.plshark.users.repo.GroupRolesRepository
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.await
import org.springframework.stereotype.Repository

@Repository
class SpringDataGroupRolesRepository(private val client: DatabaseClient) : GroupRolesRepository {

    override fun findRolesForGroup(groupId: Long): Flow<Role> {
        return client.execute(
            "SELECT r.* FROM roles r INNER JOIN group_roles ur ON r.id = ur.role_id WHERE ur.group_id = :id"
        )
            .bind("id", groupId)
            .map { row -> SpringDataRolesRepository.mapRow(row) }
            .all()
            .asFlow()
    }

    override suspend fun insert(groupId: Long, roleId: Long) {
        return client.execute("INSERT INTO group_roles (group_id, role_id) values (:groupId, :roleId)")
            .bind("groupId", groupId)
            .bind("roleId", roleId)
            .await()
    }

    override suspend fun deleteById(groupId: Long, roleId: Long) {
        return client.execute("DELETE FROM group_roles WHERE group_id = :groupId AND role_id = :roleId")
            .bind("groupId", groupId)
            .bind("roleId", roleId)
            .await()
    }

    override suspend fun deleteByGroupId(groupId: Long) {
        return client.execute("DELETE FROM group_roles WHERE group_id = :groupId")
            .bind("groupId", groupId)
            .await()
    }

    override suspend fun deleteByRoleId(roleId: Long) {
        return client.execute("DELETE FROM group_roles WHERE role_id = :roleId")
            .bind("roleId", roleId)
            .await()
    }
}
