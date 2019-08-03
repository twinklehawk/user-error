package net.plshark.users.repo.springdata;

import java.util.Objects;
import java.util.Optional;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import net.plshark.users.model.Role;
import net.plshark.users.repo.RolesRepository;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Role repository that uses spring data and r2dbc
 */
@Repository
public class SpringDataRolesRepository implements RolesRepository {

    private final DatabaseClient client;

    public SpringDataRolesRepository(DatabaseClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public Mono<Role> getForId(long id) {
        return client.execute()
                .sql("SELECT * FROM roles WHERE id = :id")
                .bind("id", id)
                .map(this::mapRow)
                .one();
    }

    @Override
    public Mono<Role> getForName(String name) {
        return client.execute()
                .sql("SELECT * FROM roles WHERE name = :name")
                .bind("name", name)
                .map(this::mapRow)
                .one();
    }

    @Override
    public Flux<Role> getRoles(int maxResults, long offset) {
        if (maxResults < 1)
            throw new IllegalArgumentException("Max results must be greater than 0");
        if (offset < 0)
            throw new IllegalArgumentException("Offset cannot be negative");

        String sql = "SELECT * FROM roles ORDER BY id OFFSET " + offset + " ROWS FETCH FIRST " + maxResults + " ROWS ONLY";
        return client.execute()
                .sql(sql)
                .map(this::mapRow)
                .all();
    }

    @Override
    public Mono<Role> insert(Role role) {
        if (role.getId() != null)
            throw new IllegalArgumentException("Cannot insert role with ID already set");

        return client.execute()
                .sql("INSERT INTO roles (name, application) VALUES (:name, :application) RETURNING id")
                .bind("name", role.getName())
                .bind("application", role.getApplication())
                .fetch().one()
                .flatMap(map -> Optional.ofNullable((Long) map.get("id"))
                        .map(Mono::just)
                        .orElse(Mono.empty()))
                .switchIfEmpty(Mono.error(() -> new IllegalStateException("No ID returned from insert")))
                .map(id -> Role.create(id, role.getName(), role.getApplication()));
    }

    @Override
    public Mono<Void> delete(long roleId) {
        return client.execute()
                .sql("DELETE FROM roles WHERE id = :id")
                .bind("id", roleId)
                .then();
    }

    private Role mapRow(Row row, RowMetadata rowMetadata) {
        return Role.create(row.get("id", Long.class), row.get("name", String.class),
                row.get("application", String.class));
    }
}
