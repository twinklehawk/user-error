package net.plshark.users.service;

import java.util.Objects;
import net.plshark.users.model.Group;
import net.plshark.users.repo.GroupRolesRepository;
import net.plshark.users.repo.GroupsRepository;
import net.plshark.users.repo.UserGroupsRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Group management service implementation
 */
@Component
public class GroupManagementServiceImpl implements GroupManagementService {

    private final GroupsRepository groupsRepo;
    private final UserGroupsRepository userGroupsRepo;
    private final GroupRolesRepository groupRolesRepo;

    public GroupManagementServiceImpl(GroupsRepository groupsRepo, UserGroupsRepository userGroupsRepo,
                                      GroupRolesRepository groupRolesRepo) {
        this.groupsRepo = Objects.requireNonNull(groupsRepo, "groupsRepo cannot be null");
        this.userGroupsRepo = Objects.requireNonNull(userGroupsRepo, "userGroupsRepo cannot be null");
        this.groupRolesRepo = Objects.requireNonNull(groupRolesRepo, "groupRolesRepo cannot be null");
    }

    @Override
    public Mono<Group> getGroupByName(String name) {
        return groupsRepo.getForName(name);
    }

    @Override
    public Flux<Group> getGroups(int maxResults, long offset) {
        return groupsRepo.getGroups(maxResults, offset);
    }

    @Override
    public Mono<Group> insertGroup(Group group) {
        return groupsRepo.insert(group);
    }

    @Override
    public Mono<Void> deleteGroup(long groupId) {
        return userGroupsRepo.deleteUserGroupsForGroup(groupId)
                .then(groupRolesRepo.deleteForGroup(groupId))
                .then(groupsRepo.delete(groupId));
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
