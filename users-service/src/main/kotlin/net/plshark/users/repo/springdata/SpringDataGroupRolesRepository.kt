package net.plshark.users.repo.springdata

import net.plshark.users.model.Role
import net.plshark.users.repo.GroupRolesRepository
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class SpringDataGroupRolesRepository(private val client: DatabaseClient) : GroupRolesRepository {

    override fun getRolesForGroup(groupId: Long): Flux<Role> {
        return client.execute("SELECT r.* FROM roles r INNER JOIN group_roles ur ON r.id = ur.role_id WHERE ur.group_id = :id")
            .bind("id", groupId)
            .map { row -> SpringDataRolesRepository.mapRow(row) }
            .all()
    }

    override fun insert(groupId: Long, roleId: Long): Mono<Void> {
        return client.execute("INSERT INTO group_roles (group_id, role_id) values (:groupId, :roleId)")
            .bind("groupId", groupId)
            .bind("roleId", roleId)
            .then()
    }

    override fun delete(groupId: Long, roleId: Long): Mono<Void> {
        return client.execute("DELETE FROM group_roles WHERE group_id = :groupId AND role_id = :roleId")
            .bind("groupId", groupId)
            .bind("roleId", roleId)
            .then()
    }

    override fun deleteForGroup(groupId: Long): Mono<Void> {
        return client.execute("DELETE FROM group_roles WHERE group_id = :groupId")
            .bind("groupId", groupId)
            .then()
    }

    override fun deleteForRole(roleId: Long): Mono<Void> {
        return client.execute("DELETE FROM group_roles WHERE role_id = :roleId")
            .bind("roleId", roleId)
            .then()
    }
}
