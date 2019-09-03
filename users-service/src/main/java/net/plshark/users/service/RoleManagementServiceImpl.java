package net.plshark.users.service;

import java.util.Objects;
import net.plshark.users.model.Role;
import net.plshark.users.repo.GroupRolesRepository;
import net.plshark.users.repo.RolesRepository;
import net.plshark.users.repo.UserRolesRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Role management service implementation
 */
@Component
public class RoleManagementServiceImpl implements RoleManagementService {

    private final RolesRepository roleRepo;
    private final UserRolesRepository userRolesRepo;
    private final GroupRolesRepository groupRolesRepo;

    public RoleManagementServiceImpl(RolesRepository roleRepository, UserRolesRepository userRolesRepo,
                                     GroupRolesRepository groupRolesRepo) {
        this.roleRepo = Objects.requireNonNull(roleRepository, "roleRepository cannot be null");
        this.userRolesRepo = Objects.requireNonNull(userRolesRepo, "userRolesRepo cannot be null");
        this.groupRolesRepo = Objects.requireNonNull(groupRolesRepo, "groupRolesRepo cannot be null");
    }

    @Override
    public Mono<Role> insertRole(Role role) {
        return roleRepo.insert(role);
    }

    @Override
    public Mono<Void> deleteRole(long roleId) {
        return userRolesRepo.deleteUserRolesForRole(roleId)
                .then(groupRolesRepo.deleteForRole(roleId))
                .then(roleRepo.delete(roleId));
    }

    @Override
    public Mono<Role> getRoleByName(String name, String application) {
        return roleRepo.getForName(name, application);
    }

    @Override
    public Flux<Role> getRoles(int maxResults, long offset) {
        return roleRepo.getRoles(maxResults, offset);
    }
}
