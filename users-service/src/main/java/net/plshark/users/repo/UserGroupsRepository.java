package net.plshark.users.repo;

import net.plshark.users.model.Group;
import net.plshark.users.model.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserGroupsRepository {

    /**
     * Get all the groups a user belongs to
     * @param userId the ID of the user
     * @return the groups
     */
    Flux<Group> getGroupsForUser(long userId);

    /**
     * Get all roles a user has through the groups a user belongs to
     * @param userId the ID of the user
     * @return the roles
     */
    Flux<Role> getGroupRolesForUser(long userId);

    /**
     * Add a user to a group
     * @param userId the user ID
     * @param groupId the group ID
     * @return when complete
     */
    Mono<Void> insert(long userId, long groupId);

    /**
     * Remove a user from a group
     * @param userId the user ID
     * @param groupId the group ID
     * @return when complete
     */
    Mono<Void> delete(long userId, long groupId);

    /**
     * Remove a user from all groups
     * @param userId the user ID
     * @return when complete
     */
    Mono<Void> deleteUserGroupsForUser(long userId);

    /**
     * Remove a group from all users
     * @param groupId the group ID
     * @return when complete
     */
    Mono<Void> deleteUserGroupsForGroup(long groupId);
}
