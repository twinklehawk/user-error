package net.plshark.users.service;

import net.plshark.users.model.Group;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service for managing groups
 */
public interface GroupsService {

    /**
     * Retrieve a group by name
     * @param name the group name
     * @return the matching group
     */
    Mono<Group> get(String name);

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
     * @return the saved group or a {@link net.plshark.errors.DuplicateException} if a group with the same name already
     * exists
     */
    Mono<Group> create(Group group);

    /**
     * Delete a group
     * @param groupId the group ID
     * @return when complete
     */
    Mono<Void> delete(long groupId);

    /**
     * Delete a group
     * @param name the group name
     * @return when complete
     */
    Mono<Void> delete(String name);

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
