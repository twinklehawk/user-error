package net.plshark.users.service;

import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import net.plshark.errors.DuplicateException;
import net.plshark.errors.ObjectNotFoundException;
import net.plshark.users.model.Group;
import net.plshark.users.repo.GroupRolesRepository;
import net.plshark.users.repo.GroupsRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Group management service implementation
 */
@Component
@AllArgsConstructor
public class GroupsServiceImpl implements GroupsService {

    @Nonnull
    private final GroupsRepository groupsRepo;
    @Nonnull
    private final GroupRolesRepository groupRolesRepo;

    @Override
    public Mono<Group> get(String name) {
        return groupsRepo.getForName(name);
    }

    @Override
    public Mono<Group> getRequired(String name) {
        return get(name).switchIfEmpty(Mono.error(() -> new ObjectNotFoundException("No group found for " + name)));
    }

    @Override
    public Flux<Group> getGroups(int maxResults, long offset) {
        return groupsRepo.getGroups(maxResults, offset);
    }

    @Override
    public Mono<Group> create(Group group) {
        return groupsRepo.insert(group)
                .onErrorMap(DataIntegrityViolationException.class, e -> new DuplicateException("A group with name " +
                        group.getName() + " already exists", e));
    }

    @Override
    public Mono<Void> delete(long groupId) {
        return groupsRepo.delete(groupId);
    }

    @Override
    public Mono<Void> delete(String name) {
        //noinspection ConstantConditions
        return get(name)
                .flatMap(group -> delete(group.getId()));
    }

    @Override
    public Mono<Void> addRoleToGroup(long groupId, long roleId) {
        return groupRolesRepo.insert(groupId, roleId);
    }

    @Override
    public Mono<Void> removeRoleFromGroup(long groupId, long roleId) {
        return groupRolesRepo.delete(groupId, roleId);
    }
}
