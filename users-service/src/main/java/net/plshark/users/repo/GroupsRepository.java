package net.plshark.users.repo;

import net.plshark.users.model.Group;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository for groups
 */
public interface GroupsRepository {

    /**
     * Get a group by ID
     * @param id the group ID
     * @return the matching group if found
     */
    Mono<Group> getForId(long id);

    /**
     * Get a group by name
     * @param name the group name
     * @return the matching group if found
     */
    Mono<Group> getForName(String name);

    /**
     * Get all groups up to a maximum number of results
     * @param maxResults the max results to return
     * @param offset the offset to start at
     * @return the groups
     */
    Flux<Group> getGroups(int maxResults, long offset);

    /**
     * Save a new group
     * @param group the group to save
     * @return the saved group
     */
    Mono<Group> insert(Group group);

    /**
     * Delete a group by ID
     * @param groupId the group ID
     * @return when complete
     */
    Mono<Void> delete(long groupId);
}
