package net.plshark.users.repo.springdata;

import java.util.Objects;
import java.util.Optional;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import net.plshark.users.model.User;
import net.plshark.users.repo.UsersRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * User repository that uses spring data and r2dbc
 */
@Repository
public class SpringDataUsersRepository implements UsersRepository {

    private final DatabaseClient client;

    public SpringDataUsersRepository(DatabaseClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public Mono<User> getForUsername(String username) {
        Objects.requireNonNull(username, "username cannot be null");
        return client.execute()
                .sql("SELECT * FROM users WHERE username = :username")
                .bind("username", username)
                .map(SpringDataUsersRepository::mapRow)
                .one();
    }

    @Override
    public Mono<User> insert(User user) {
        if (user.getId() != null)
            throw new IllegalArgumentException("Cannot insert user with ID already set");

        return client.execute()
                .sql("INSERT INTO users (username, password) VALUES (:username, :password) RETURNING id")
                .bind("username", user.getUsername())
                .bind("password", user.getPassword())
                .fetch().one()
                .flatMap(map -> Optional.ofNullable((Long) map.get("id"))
                        .map(Mono::just)
                        .orElse(Mono.empty()))
                .switchIfEmpty(Mono.error(() -> new IllegalStateException("No ID returned from insert")))
                .map(id -> User.create(id, user.getUsername(), user.getPassword()));
    }

    @Override
    public Mono<Void> delete(long userId) {
        return client.execute()
                .sql("DELETE FROM users WHERE id = :id")
                .bind("id", userId)
                .then();
    }

    @Override
    public Mono<User> getForId(long id) {
        return client.execute()
                .sql("SELECT * FROM users WHERE id = :id")
                .bind("id", id)
                .map(SpringDataUsersRepository::mapRow)
                .one();
    }

    @Override
    public Mono<Void> updatePassword(long id, String currentPassword, String newPassword) {
        return client.execute()
                .sql("UPDATE users SET password = :newPassword WHERE id = :id AND password = :oldPassword")
                .bind("newPassword", newPassword)
                .bind("id", id)
                .bind("oldPassword", currentPassword)
                .fetch().rowsUpdated()
                .flatMap(updates -> updates == 0 ? Mono.error(() -> new EmptyResultDataAccessException("No matching user for password update", 1)) : Mono.just(updates))
                .then();
    }

    @Override
    public Flux<User> getAll(int maxResults, long offset) {
        if (maxResults < 1)
            throw new IllegalArgumentException("Max results must be greater than 0");
        if (offset < 0)
            throw new IllegalArgumentException("Offset cannot be negative");

        String sql = "SELECT * FROM users ORDER BY id OFFSET " + offset + " ROWS FETCH FIRST " + maxResults + " ROWS ONLY";
        return client.execute()
                .sql(sql)
                .map(SpringDataUsersRepository::mapRow)
                .all();
    }

    static User mapRow(Row row, RowMetadata rowMetadata) {
        return User.create(row.get("id", Long.class), row.get("username", String.class), row.get("password", String.class));
    }
}
