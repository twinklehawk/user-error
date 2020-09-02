package net.plshark.users.repo.springdata

import net.plshark.users.model.Group
import net.plshark.users.model.Role
import net.plshark.users.repo.UserGroupsRepository
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * User groups repository using spring data
 */
@Repository
class SpringDataUserGroupsRepository(private val client: DatabaseClient) : UserGroupsRepository {

    override fun findGroupsByUserId(userId: Long): Flux<Group> {
        return client.execute("SELECT * FROM groups g INNER JOIN user_groups ug ON g.id = ug.group_id WHERE " +
                "ug.user_id = :id")
            .bind("id", userId)
            .map { row -> SpringDataGroupsRepository.mapRow(row) }
            .all()
    }

    override fun findGroupRolesByUserId(userId: Long): Flux<Role> {
        return client.execute("SELECT r.* from roles r, user_groups ug, group_roles gr WHERE " +
                "ug.user_id = :userId AND gr.group_id = ug.group_id AND r.id = gr.role_id")
            .bind("userId", userId)
            .map { row -> SpringDataRolesRepository.mapRow(row) }
            .all()
    }

    override fun insert(userId: Long, groupId: Long): Mono<Void> {
        return client.execute("INSERT INTO user_groups (user_id, group_id) values (:userId, :groupId)")
            .bind("userId", userId)
            .bind("groupId", groupId)
            .then()
    }

    override fun deleteById(userId: Long, groupId: Long): Mono<Void> {
        return client.execute("DELETE FROM user_groups WHERE user_id = :userId AND group_id = :groupId")
            .bind("userId", userId)
            .bind("groupId", groupId)
            .then()
    }
}
