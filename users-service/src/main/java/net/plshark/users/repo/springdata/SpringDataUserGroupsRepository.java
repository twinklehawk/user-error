package net.plshark.users.repo.springdata;

import java.util.Objects;
import net.plshark.users.model.Group;
import net.plshark.users.repo.UserGroupsRepository;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * User groups repository using spring data
 */
@Repository
public class SpringDataUserGroupsRepository implements UserGroupsRepository {

    private final DatabaseClient client;

    public SpringDataUserGroupsRepository(DatabaseClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public Flux<Group> getGroupsForUser(long userId) {
        return client.execute()
                .sql("SELECT * FROM groups g INNER JOIN user_groups ug ON g.id = ug.group_id WHERE ug.user_id = :id")
                .bind("id", userId)
                .map(SpringDataGroupsRepository::mapRow)
                .all();
    }

    @Override
    public Mono<Void> insert(long userId, long groupId) {
        return client.execute()
                .sql("INSERT INTO user_groups (user_id, group_id) values (:userId, :groupId)")
                .bind("userId", userId)
                .bind("groupId", groupId)
                .then();
    }

    @Override
    public Mono<Void> delete(long userId, long groupId) {
        return client.execute()
                .sql("DELETE FROM user_groups WHERE user_id = :userId AND group_id = :groupId")
                .bind("userId", userId)
                .bind("groupId", groupId)
                .then();
    }

    @Override
    public Mono<Void> deleteUserGroupsForUser(long userId) {
        return client.execute()
                .sql("DELETE FROM user_groups WHERE user_id = :userId")
                .bind("userId", userId)
                .then();
    }

    @Override
    public Mono<Void> deleteUserGroupsForGroup(long groupId) {
        return client.execute()
                .sql("DELETE FROM user_groups WHERE group_id = :groupId")
                .bind("groupId", groupId)
                .then();
    }
}
