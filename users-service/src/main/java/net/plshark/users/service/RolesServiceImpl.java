package net.plshark.users.service;

import java.util.Objects;
import net.plshark.errors.DuplicateException;
import net.plshark.users.model.Role;
import net.plshark.users.repo.ApplicationsRepository;
import net.plshark.users.repo.GroupRolesRepository;
import net.plshark.users.repo.RolesRepository;
import net.plshark.users.repo.UserRolesRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Role management service implementation
 */
@Component
public class RolesServiceImpl implements RolesService {

    private final RolesRepository rolesRepo;
    private final ApplicationsRepository appsRepo;
    private final UserRolesRepository userRolesRepo;
    private final GroupRolesRepository groupRolesRepo;

    public RolesServiceImpl(RolesRepository rolesRepo, ApplicationsRepository appsRepo,
                            UserRolesRepository userRolesRepo, GroupRolesRepository groupRolesRepo) {
        this.rolesRepo = Objects.requireNonNull(rolesRepo, "rolesRepo cannot be null");
        this.appsRepo = Objects.requireNonNull(appsRepo, "appsRepo cannot be null");
        this.userRolesRepo = Objects.requireNonNull(userRolesRepo, "userRolesRepo cannot be null");
        this.groupRolesRepo = Objects.requireNonNull(groupRolesRepo, "groupRolesRepo cannot be null");
    }


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
        return userRolesRepo.deleteUserRolesForRole(roleId)
                .then(groupRolesRepo.deleteForRole(roleId))
                .then(rolesRepo.delete(roleId));
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
    public Flux<Role> getRoles(int maxResults, long offset) {
        return rolesRepo.getRoles(maxResults, offset);
    }
}
