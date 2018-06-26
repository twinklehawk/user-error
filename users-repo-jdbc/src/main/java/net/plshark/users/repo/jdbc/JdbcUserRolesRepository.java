package net.plshark.users.repo.jdbc;

import java.util.Objects;

import javax.inject.Named;
import javax.inject.Singleton;

import net.plshark.jdbc.ReactiveUtils;
import net.plshark.users.Role;
import net.plshark.users.repo.UserRolesRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * User roles repository that uses JDBC
 */
@Named
@Singleton
public class JdbcUserRolesRepository implements UserRolesRepository {

    private final SyncJdbcUserRolesRepository syncRepo;

    /**
     * Create a new instance
     * @param syncRepo the synchronous repository to wrap
     */
    public JdbcUserRolesRepository(SyncJdbcUserRolesRepository syncRepo) {
        this.syncRepo = Objects.requireNonNull(syncRepo, "syncRepo cannot be null");
    }

    @Override
    public Flux<Role> getRolesForUser(long userId) {
        return ReactiveUtils.wrapWithFlux(() -> syncRepo.getRolesForUser(userId));
    }

    @Override
    public Mono<Void> insertUserRole(long userId, long roleId) {
        return ReactiveUtils.wrapWithMono(() -> {
            syncRepo.insertUserRole(userId, roleId);
            return null;
        });
    }

    @Override
    public Mono<Void> deleteUserRole(long userId, long roleId) {
        return ReactiveUtils.wrapWithMono(() -> {
            syncRepo.deleteUserRole(userId, roleId);
            return null;
        });
    }

    @Override
    public Mono<Void> deleteUserRolesForUser(long userId) {
        return ReactiveUtils.wrapWithMono(() -> {
            syncRepo.deleteUserRolesForUser(userId);
            return null;
        });
    }

    @Override
    public Mono<Void> deleteUserRolesForRole(long roleId) {
        return ReactiveUtils.wrapWithMono(() -> {
            syncRepo.deleteUserRolesForRole(roleId);
            return null;
        });
    }
}
