package net.plshark.users.repo.springdata;

import java.util.Objects;
import java.util.Optional;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import net.plshark.users.model.Application;
import net.plshark.users.repo.ApplicationsRepository;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Role repository that uses spring data and r2dbc
 */
@Repository
public class SpringDataApplicationsRepository implements ApplicationsRepository {

    private final DatabaseClient client;

    public SpringDataApplicationsRepository(DatabaseClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public Mono<Application> get(long id) {
        return client.execute("SELECT * FROM applications WHERE id = :id")
                .bind("id", id)
                .map(SpringDataApplicationsRepository::mapRow)
                .one();
    }

    @Override
    public Mono<Application> get(String name) {
        return client.execute("SELECT * FROM applications WHERE name = :name")
                .bind("name", name)
                .map(SpringDataApplicationsRepository::mapRow)
                .one();
    }

    @Override
    public Mono<Application> insert(Application application) {
        if (application.getId() != null)
            throw new IllegalArgumentException("Cannot insert application with ID already set");

        return client.execute("INSERT INTO applications (name) VALUES (:name) RETURNING id")
                .bind("name", application.getName())
                .fetch().one()
                .flatMap(map -> Optional.ofNullable((Long) map.get("id"))
                        .map(Mono::just)
                        .orElse(Mono.empty()))
                .switchIfEmpty(Mono.error(() -> new IllegalStateException("No ID returned from insert")))
                .map(id -> application.toBuilder().id(id).build());
    }

    @Override
    public Mono<Void> delete(long id) {
        return client.execute("DELETE FROM applications WHERE id = :id")
                .bind("id", id)
                .then();
    }

    @Override
    public Mono<Void> delete(String name) {
        return client.execute("DELETE FROM applications WHERE name = :name")
                .bind("name", name)
                .then();
    }

    static Application mapRow(Row row, RowMetadata rowMetadata) {
        return Application.builder()
                .id(row.get("id", Long.class))
                .name(row.get("name", String.class))
                .build();
    }
}
