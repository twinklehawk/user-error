package net.plshark.users.service;

import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import net.plshark.errors.DuplicateException;
import net.plshark.errors.ObjectNotFoundException;
import net.plshark.users.model.Role;
import net.plshark.users.repo.ApplicationsRepository;
import net.plshark.users.repo.RolesRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Role management service implementation
 */
@Component
@AllArgsConstructor
public class RolesServiceImpl implements RolesService {

    @Nonnull
    private final RolesRepository rolesRepo;
    @Nonnull
    private final ApplicationsRepository appsRepo;

    @Override
    public Mono<Role> create(Role role) {
        return rolesRepo.insert(role)
                .onErrorMap(DataIntegrityViolationException.class, e -> new DuplicateException("A role with name " +
                        role.getName() + " already exists", e));
    }

    @Override
    public Mono<Role> create(String application, Role role) {
        return appsRepo.get(application)
                // TODO handle no application found
                .flatMap(app -> create(role.toBuilder().applicationId(app.getId()).build()));
    }

    @Override
    public Mono<Void> delete(long roleId) {
        return rolesRepo.delete(roleId);
    }

    @Override
    public Mono<Void> delete(String application, String name) {
        //noinspection ConstantConditions
        return get(application, name)
                .flatMap(role -> delete(role.getId()));
    }

    @Override
    public Mono<Role> get(String application, String name) {
        //noinspection ConstantConditions
        return appsRepo.get(application)
                .flatMap(app -> rolesRepo.get(app.getId(), name));
    }

    @Override
    public Mono<Role> getRequired(String application, String name) {
        return get(application, name)
                .switchIfEmpty(Mono.error(() -> new ObjectNotFoundException("No role found for " + application +":" + name)));
    }

    @Override
    public Flux<Role> getRoles(int maxResults, long offset) {
        return rolesRepo.getRoles(maxResults, offset);
    }
}
