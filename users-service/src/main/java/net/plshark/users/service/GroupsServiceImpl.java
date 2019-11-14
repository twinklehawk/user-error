package net.plshark.users.service;

import java.util.Objects;
import net.plshark.errors.DuplicateException;
import net.plshark.users.model.Group;
import net.plshark.users.repo.GroupRolesRepository;
import net.plshark.users.repo.GroupsRepository;
import net.plshark.users.repo.UserGroupsRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Group management service implementation
 */
@Component
public class GroupsServiceImpl implements GroupsService {

    private final GroupsRepository groupsRepo;
    private final UserGroupsRepository userGroupsRepo;
    private final GroupRolesRepository groupRolesRepo;

    public GroupsServiceImpl(GroupsRepository groupsRepo, UserGroupsRepository userGroupsRepo,
                             GroupRolesRepository groupRolesRepo) {
        this.groupsRepo = Objects.requireNonNull(groupsRepo, "groupsRepo cannot be null");
        this.userGroupsRepo = Objects.requireNonNull(userGroupsRepo, "userGroupsRepo cannot be null");
        this.groupRolesRepo = Objects.requireNonNull(groupRolesRepo, "groupRolesRepo cannot be null");
    }

    @Override
    public Mono<Group> get(String name) {
        return groupsRepo.getForName(name);
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
        return userGroupsRepo.deleteUserGroupsForGroup(groupId)
                .then(groupRolesRepo.deleteForGroup(groupId))
                .then(groupsRepo.delete(groupId));
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
