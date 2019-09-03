package net.plshark.users.service;

import net.plshark.users.model.Group;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service for managing groups
 */
public interface GroupManagementService {

    /**
     * Retrieve a group by name
     * @param name the group name
     * @return the matching group
     */
    Mono<Group> getGroupByName(String name);

    /**
     * Get all groups up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return the group
     */
    Flux<Group> getGroups(int maxResults, long offset);

    /**
     * Save a new group
     * @param group the group
     * @return the saved group
     */
    Mono<Group> insertGroup(Group group);

    /**
     * Delete a group
     * @param groupId the group ID
     * @return when complete
     */
    Mono<Void> deleteGroup(long groupId);

    /**
     * Add a role to a group
     * @param groupId the group ID
     * @param roleId the role ID
     * @return when complete
     */
    Mono<Void> addRoleToGroup(long groupId, long roleId);

    /**
     * Remove a role from a group
     * @param groupId the group ID
     * @param roleId the role ID
     * @return when complete
     */
    Mono<Void> removeRoleFromGroup(long groupId, long roleId);
}
