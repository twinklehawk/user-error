package net.plshark.users.repo.springdata;

import java.util.Objects;
import net.plshark.users.model.Role;
import net.plshark.users.repo.GroupRolesRepository;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class SpringDataGroupRolesRepository implements GroupRolesRepository {

    private final DatabaseClient client;

    public SpringDataGroupRolesRepository(DatabaseClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public Flux<Role> getRolesForGroup(long groupId) {
        return client.execute("SELECT id, name, application FROM roles r INNER JOIN group_roles ur ON r.id = ur.role_id WHERE ur.group_id = :id")
                .bind("id", groupId)
                .map(SpringDataRolesRepository::mapRow)
                .all();
    }

    @Override
    public Mono<Void> insert(long groupId, long roleId) {
        return client.execute("INSERT INTO group_roles (group_id, role_id) values (:groupId, :roleId)")
                .bind("groupId", groupId)
                .bind("roleId", roleId)
                .then();
    }

    @Override
    public Mono<Void> delete(long groupId, long roleId) {
        return client.execute("DELETE FROM group_roles WHERE group_id = :groupId AND role_id = :roleId")
                .bind("groupId", groupId)
                .bind("roleId", roleId)
                .then();
    }

    @Override
    public Mono<Void> deleteForGroup(long groupId) {
        return client.execute("DELETE FROM group_roles WHERE group_id = :groupId")
                .bind("groupId", groupId)
                .then();
    }

    @Override
    public Mono<Void> deleteForRole(long roleId) {
        return client.execute("DELETE FROM group_roles WHERE role_id = :roleId")
                .bind("roleId", roleId)
                .then();
    }
}
