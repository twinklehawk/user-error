package net.plshark.users.repo.jdbc;

import java.util.Objects;
import javax.inject.Named;
import javax.inject.Singleton;

import net.plshark.jdbc.ReactiveUtils;
import net.plshark.users.User;
import net.plshark.users.repo.UsersRepository;
import reactor.core.publisher.Mono;

/**
 * User repository that uses JDBC
 */
@Named
@Singleton
public class JdbcUsersRepository implements UsersRepository {

    private final SyncJdbcUsersRepository syncRepo;

    /**
     * Create a new instance
     * @param syncRepo the synchronous repository to wrap
     */
    public JdbcUsersRepository(SyncJdbcUsersRepository syncRepo) {
        this.syncRepo = Objects.requireNonNull(syncRepo, "syncRepo cannot be null");
    }

    @Override
    public Mono<User> getForUsername(String username) {
        return ReactiveUtils.wrapWithMono(() -> syncRepo.getForUsername(username).orElse(null));
    }

    @Override
    public Mono<User> insert(User user) {
        return ReactiveUtils.wrapWithMono(() -> syncRepo.insert(user));
    }

    @Override
    public Mono<Void> delete(long userId) {
        return ReactiveUtils.wrapWithMono(() -> {
            syncRepo.delete(userId);
            return null;
        });
    }

    @Override
    public Mono<User> getForId(long id) {
        return ReactiveUtils.wrapWithMono(() -> syncRepo.getForId(id).orElse(null));
    }

    @Override
    public Mono<Void> updatePassword(long id, String currentPassword, String newPassword) {
        return ReactiveUtils.wrapWithMono(() -> {
            syncRepo.updatePassword(id, currentPassword, newPassword);
            return null;
        });
    }
}
