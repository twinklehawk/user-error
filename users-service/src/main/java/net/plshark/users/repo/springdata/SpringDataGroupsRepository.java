package net.plshark.users.repo.springdata;

import java.util.Objects;
import java.util.Optional;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import net.plshark.users.model.Group;
import net.plshark.users.model.Role;
import net.plshark.users.repo.GroupsRepository;
import net.plshark.users.repo.RolesRepository;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class SpringDataGroupsRepository implements GroupsRepository {

    private final DatabaseClient client;

    public SpringDataGroupsRepository(DatabaseClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public Mono<Group> getForId(long id) {
        return client.execute()
                .sql("SELECT * FROM groups WHERE id = :id")
                .bind("id", id)
                .map(SpringDataGroupsRepository::mapRow)
                .one();
    }

    @Override
    public Mono<Group> getForName(String name) {
        return client.execute()
                .sql("SELECT * FROM groups WHERE name = :name")
                .bind("name", name)
                .map(SpringDataGroupsRepository::mapRow)
                .one();
    }

    @Override
    public Flux<Group> getRoles(int maxResults, long offset) {
        if (maxResults < 1)
            throw new IllegalArgumentException("Max results must be greater than 0");
        if (offset < 0)
            throw new IllegalArgumentException("Offset cannot be negative");

        String sql = "SELECT * FROM roles ORDER BY id OFFSET " + offset + " ROWS FETCH FIRST " + maxResults + " ROWS ONLY";
        return client.execute()
                .sql(sql)
                .map(SpringDataGroupsRepository::mapRow)
                .all();
    }

    @Override
    public Mono<Group> insert(Group group) {
        if (group.getId() != null)
            throw new IllegalArgumentException("Cannot insert group with ID already set");

        return client.execute()
                .sql("INSERT INTO groups (name) VALUES (:name) RETURNING id")
                .bind("name", group.getName())
                .fetch().one()
                .flatMap(map -> Optional.ofNullable((Long) map.get("id"))
                        .map(Mono::just)
                        .orElse(Mono.empty()))
                .switchIfEmpty(Mono.error(() -> new IllegalStateException("No ID returned from insert")))
                .map(id -> Group.create(id, group.getName()));
    }

    @Override
    public Mono<Void> delete(long groupId) {
        return client.execute()
                .sql("DELETE FROM groups WHERE id = :id")
                .bind("id", groupId)
                .then();
    }

    static Group mapRow(Row row, RowMetadata rowMetadata) {
        return Group.create(row.get("id", Long.class), row.get("name", String.class));
    }
}
