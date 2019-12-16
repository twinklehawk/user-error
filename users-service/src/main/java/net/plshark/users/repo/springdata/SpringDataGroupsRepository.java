package net.plshark.users.repo.springdata;

import java.util.Objects;
import java.util.Optional;
import io.r2dbc.spi.Row;
import net.plshark.users.model.Group;
import net.plshark.users.repo.GroupsRepository;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Groups repository using spring data
 */
@Repository
public class SpringDataGroupsRepository implements GroupsRepository {

    private final DatabaseClient client;

    public SpringDataGroupsRepository(DatabaseClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public Mono<Group> getForId(long id) {
        return client.execute("SELECT * FROM groups WHERE id = :id")
                .bind("id", id)
                .map(SpringDataGroupsRepository::mapRow)
                .one();
    }

    @Override
    public Mono<Group> getForName(String name) {
        return client.execute("SELECT * FROM groups WHERE name = :name")
                .bind("name", name)
                .map(SpringDataGroupsRepository::mapRow)
                .one();
    }

    @Override
    public Flux<Group> getGroups(int maxResults, long offset) {
        if (maxResults < 1)
            throw new IllegalArgumentException("Max results must be greater than 0");
        if (offset < 0)
            throw new IllegalArgumentException("Offset cannot be negative");

        String sql = "SELECT * FROM groups ORDER BY id OFFSET " + offset + " ROWS FETCH FIRST " + maxResults + " ROWS ONLY";
        return client.execute(sql)
                .map(SpringDataGroupsRepository::mapRow)
                .all();
    }

    @Override
    public Mono<Group> insert(Group group) {
        if (group.getId() != null)
            throw new IllegalArgumentException("Cannot insert group with ID already set");

        return client.execute("INSERT INTO groups (name) VALUES (:name) RETURNING id")
                .bind("name", group.getName())
                .fetch().one()
                .flatMap(map -> Optional.ofNullable((Long) map.get("id"))
                        .map(Mono::just)
                        .orElse(Mono.empty()))
                .switchIfEmpty(Mono.error(() -> new IllegalStateException("No ID returned from insert")))
                .map(id -> group.toBuilder().id(id).build());
    }

    @Override
    public Mono<Void> delete(long groupId) {
        return client.execute("DELETE FROM groups WHERE id = :id")
                .bind("id", groupId)
                .then();
    }

    /**
     * Map a database row to a {@link Group}
     * @param row the database row
     * @return the mapped group
     */
    static Group mapRow(Row row) {
        return Group.builder()
                .id(row.get("id", Long.class))
                .name(row.get("name", String.class))
                .build();
    }
}
