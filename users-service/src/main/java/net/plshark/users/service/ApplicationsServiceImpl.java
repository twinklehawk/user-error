package net.plshark.users.service;

import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import net.plshark.errors.DuplicateException;
import net.plshark.users.model.Application;
import net.plshark.users.model.Role;
import net.plshark.users.repo.ApplicationsRepository;
import net.plshark.users.repo.RolesRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Applications management service implementation
 */
@Component
@AllArgsConstructor
public class ApplicationsServiceImpl implements ApplicationsService {

    @Nonnull
    private final ApplicationsRepository appsRepo;
    @Nonnull
    private final RolesRepository rolesRepo;

    @Override
    public Mono<Application> get(String name) {
        return appsRepo.get(name);
    }

    @Override
    public Flux<Application> getApplications(int limit, long offset) {
        // TODO
        return Flux.empty();
    }

    @Override
    public Mono<Application> create(Application application) {
        return appsRepo.insert(application)
                .onErrorMap(DataIntegrityViolationException.class, e -> new DuplicateException("An application with name " +
                        application.getName() + " already exists", e));
    }

    @Override
    public Mono<Void> delete(String name) {
        return get(name)
                .map(Application::getId)
                .flatMap(appsRepo::delete);
    }

    Flux<Role> getApplicationRoles(long id) {
        return rolesRepo.getRolesForApplication(id);
    }
}
