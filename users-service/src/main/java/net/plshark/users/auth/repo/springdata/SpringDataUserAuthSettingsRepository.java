package net.plshark.users.auth.repo.springdata;

import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import net.plshark.users.auth.model.UserAuthSettings;
import net.plshark.users.auth.repo.UserAuthSettingsRepository;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import static org.springframework.data.r2dbc.query.Criteria.where;

@AllArgsConstructor
public class SpringDataUserAuthSettingsRepository implements UserAuthSettingsRepository {

    @Nonnull
    private final DatabaseClient client;

    @Override
    public Mono<UserAuthSettings> findByUserId(long userId) {
        return client.select()
                .from(UserAuthSettings.class)
                .matching(where("user_id").is(userId))
                .fetch()
                .one();
    }

    @Override
    public Mono<UserAuthSettings> insert(UserAuthSettings userAuthSettings) {
        if (userAuthSettings.getId() != null)
            throw new IllegalArgumentException("Cannot insert settings with ID already set");
        Objects.requireNonNull(userAuthSettings.getUserId(), "User ID cannot be null");

        return client.insert()
                .into(UserAuthSettings.class)
                .using(userAuthSettings)
                .map(row -> row.get("id", Long.class))
                .one()
                .map(id -> userAuthSettings.toBuilder().id(id).build());
    }
}
