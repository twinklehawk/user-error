package net.plshark.users.repo.springdata;

import java.util.Objects;
import net.plshark.users.model.Role;
import net.plshark.users.repo.UserRolesRepository;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * User roles repository that uses spring data and r2dbc
 */
@Repository
public class SpringDataUserRolesRepository implements UserRolesRepository {

    private final DatabaseClient client;

    public SpringDataUserRolesRepository(DatabaseClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public Flux<Role> getRolesForUser(long userId) {
        return client.execute("SELECT r.* FROM roles r INNER JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = :id")
                .bind("id", userId)
                .map(SpringDataRolesRepository::mapRow)
                .all();
    }

    @Override
    public Mono<Void> insertUserRole(long userId, long roleId) {
        return client.execute("INSERT INTO user_roles (user_id, role_id) values (:userId, :roleId)")
                .bind("userId", userId)
                .bind("roleId", roleId)
                .then();
    }

    @Override
    public Mono<Void> deleteUserRole(long userId, long roleId) {
        return client.execute("DELETE FROM user_roles WHERE user_id = :userId AND role_id = :roleId")
                .bind("userId", userId)
                .bind("roleId", roleId)
                .then();
    }

    @Override
    public Mono<Void> deleteUserRolesForUser(long userId) {
        return client.execute("DELETE FROM user_roles WHERE user_id = :userId")
                .bind("userId", userId)
                .then();
    }

    @Override
    public Mono<Void> deleteUserRolesForRole(long roleId) {
        return client.execute("DELETE FROM user_roles WHERE role_id = :roleId")
                .bind("roleId", roleId)
                .then();
    }
}
