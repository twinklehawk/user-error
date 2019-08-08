package net.plshark.users.repo;

import net.plshark.users.model.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository for saving, deleting, and retrieving roles
 */
public interface RolesRepository {

    /**
     * Get a role by ID
     * @param id the ID
     * @return the matching role
     */
    Mono<Role> getForId(long id);

    /**
     * Get a role by name
     * @param name the role name
     * @param application the application the role belongs to
     * @return the matching role
     */
    Mono<Role> getForName(String name, String application);

    /**
     * Get all roles up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return the roles
     */
    Flux<Role> getRoles(int maxResults, long offset);

    /**
     * Insert a new role
     * @param role the role to insert
     * @return the inserted role, will have the ID set
     */
    Mono<Role> insert(Role role);

    /**
     * Delete a role by ID
     * @param roleId the role ID
     * @return an empty result
     */
    Mono<Void> delete(long roleId);
}
