package net.plshark.users.repo.springdata

import net.plshark.users.model.Role
import net.plshark.users.repo.UserRolesRepository
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * User roles repository that uses spring data and r2dbc
 */
@Repository
class SpringDataUserRolesRepository(private val client: DatabaseClient) : UserRolesRepository {

    override fun getRolesForUser(userId: Long): Flux<Role> {
        return client.execute("SELECT r.* FROM roles r INNER JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = :id")
            .bind("id", userId)
            .map { row -> SpringDataRolesRepository.mapRow(row) }
            .all()
    }

    override fun insert(userId: Long, roleId: Long): Mono<Void> {
        return client.execute("INSERT INTO user_roles (user_id, role_id) values (:userId, :roleId)")
            .bind("userId", userId)
            .bind("roleId", roleId)
            .then()
    }

    override fun delete(userId: Long, roleId: Long): Mono<Void> {
        return client.execute("DELETE FROM user_roles WHERE user_id = :userId AND role_id = :roleId")
            .bind("userId", userId)
            .bind("roleId", roleId)
            .then()
    }

    override fun deleteUserRolesForUser(userId: Long): Mono<Void> {
        return client.execute("DELETE FROM user_roles WHERE user_id = :userId")
            .bind("userId", userId)
            .then()
    }

    override fun deleteUserRolesForRole(roleId: Long): Mono<Void> {
        return client.execute("DELETE FROM user_roles WHERE role_id = :roleId")
            .bind("roleId", roleId)
            .then()
    }

}