package net.plshark.users.service;

import net.plshark.users.model.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service for managing roles
 */
public interface RolesService {

    /**
     * Retrieve a role by name
     * @param application the application the role belongs to
     * @param name the role name
     * @return the matching role or empty if not found
     */
    Mono<Role> get(String application, String name);

    /**
     * Get all roles up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return the roles
     */
    Flux<Role> getRoles(int maxResults, long offset);

    /**
     * Save a new role
     * @param role the role
     * @return the saved role
     */
    Mono<Role> insert(Role role);

    /**
     * Save a new role
     * @param application the application the role should belong to
     * @param role the role
     * @return the saved role
     */
    Mono<Role> insert(String application, Role role);

    /**
     * Delete a role
     * @param application the parent application name
     * @param name the role name
     * @return an empty result
     */
    Mono<Void> delete(String application, String name);

    /**
     * Delete a role
     * @param roleId the role ID
     * @return an empty result
     */
    Mono<Void> delete(long roleId);
}
