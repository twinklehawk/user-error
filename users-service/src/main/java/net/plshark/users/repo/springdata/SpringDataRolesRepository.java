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
        return client.execute("SELECT * FROM roles WHERE id = :id")
                .bind("id", id)
                .map(SpringDataRolesRepository::mapRow)
                .one();
    }

    @Override
    public Mono<Role> getForName(String name, String application) {
        return client.execute("SELECT * FROM roles WHERE name = :name AND application = :application")
                .bind("name", name)
                .bind("application", application)
                .map(SpringDataRolesRepository::mapRow)
                .one();
    }

    @Override
    public Flux<Role> getRoles(int maxResults, long offset) {
        if (maxResults < 1)
            throw new IllegalArgumentException("Max results must be greater than 0");
        if (offset < 0)
            throw new IllegalArgumentException("Offset cannot be negative");

        String sql = "SELECT * FROM roles ORDER BY id OFFSET " + offset + " ROWS FETCH FIRST " + maxResults + " ROWS ONLY";
        return client.execute(sql)
                .map(SpringDataRolesRepository::mapRow)
                .all();
    }

    @Override
    public Mono<Role> insert(Role role) {
        if (role.getId() != null)
            throw new IllegalArgumentException("Cannot insert role with ID already set");

        return client.execute("INSERT INTO roles (name, application) VALUES (:name, :application) RETURNING id")
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
        return client.execute("DELETE FROM roles WHERE id = :id")
                .bind("id", roleId)
                .then();
    }

    static Role mapRow(Row row, RowMetadata rowMetadata) {
        return Role.create(row.get("id", Long.class), row.get("name", String.class),
                row.get("application", String.class));
    }
}
