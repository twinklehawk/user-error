package net.plshark.users.repo.jdbc;

import java.util.Objects;
import net.plshark.users.model.Role;
import net.plshark.users.repo.RolesRepository;
import net.plshark.utils.ReactiveUtils;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Role repository that uses JDBC
 */
@Repository
public class JdbcRolesRepository implements RolesRepository {

    private final SyncJdbcRolesRepository syncRepo;

    /**
     * Create a new instance
     * @param syncRepo the synchronous repo to wrap
     */
    public JdbcRolesRepository(SyncJdbcRolesRepository syncRepo) {
        this.syncRepo = Objects.requireNonNull(syncRepo, "syncRepo cannot be null");
    }

    @Override
    public Mono<Role> insert(Role role) {
        return ReactiveUtils.wrapWithMono(() -> syncRepo.insert(role));
    }

    @Override
    public Mono<Void> delete(long roleId) {
        return ReactiveUtils.wrapWithMono(() -> {
            syncRepo.delete(roleId);
            return null;
        });
    }

    @Override
    public Mono<Role> getForId(long id) {
        return ReactiveUtils.wrapWithMono(() -> syncRepo.getForId(id).orElse(null));
    }

    @Override
    public Mono<Role> getForName(String name) {
        return ReactiveUtils.wrapWithMono(() -> syncRepo.getForName(name).orElse(null));
    }

    @Override
    public Flux<Role> getRoles(int maxResults, long offset) {
        return ReactiveUtils.wrapWithFlux(() -> syncRepo.getRoles(maxResults, offset));
    }
}
