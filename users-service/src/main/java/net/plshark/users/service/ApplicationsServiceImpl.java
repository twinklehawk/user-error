package net.plshark.users.service;

import java.util.Objects;
import net.plshark.users.model.Application;
import net.plshark.users.model.Role;
import net.plshark.users.repo.ApplicationsRepository;
import net.plshark.users.repo.RolesRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Applications management service implementation
 */
@Component
public class ApplicationsServiceImpl implements ApplicationsService {

    private final ApplicationsRepository appsRepo;
    private final RolesService rolesService;
    private final RolesRepository rolesRepo;

    public ApplicationsServiceImpl(ApplicationsRepository appsRepo, RolesService rolesService, RolesRepository rolesRepo) {
        this.appsRepo = Objects.requireNonNull(appsRepo, "appsRepo cannot be null");
        this.rolesService = Objects.requireNonNull(rolesService, "rolesService cannot be null");
        this.rolesRepo = Objects.requireNonNull(rolesRepo, "rolesRepo cannot be null");
    }

    @Override
    public Mono<Application> get(String name) {
        return appsRepo.get(name);
    }

    @Override
    public Flux<Application> getApplications(int limit, long offset) {
        return Flux.empty();
    }

    @Override
    public Mono<Application> insert(Application application) {
        return appsRepo.insert(application);
    }

    @Override
    @Transactional
    public Mono<Void> delete(String name) {
        //noinspection ConstantConditions
        return get(name)
                .map(Application::getId)
                .flatMap(id -> getApplicationRoles(id)
                        .flatMap(role -> rolesService.delete(role.getId()))
                        .then(appsRepo.delete(id)));
    }

    Flux<Role> getApplicationRoles(long id) {
        return rolesRepo.getRolesForApplication(id);
    }
}
