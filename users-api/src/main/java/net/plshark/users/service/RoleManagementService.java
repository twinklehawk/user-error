package net.plshark.users.service;

import net.plshark.users.model.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service for managing roles
 */
public interface RoleManagementService {

    /**
     * Retrieve a role by name
     * @param name the role name
     * @param application the application the role belongs to
     * @return the matching role
     */
    Mono<Role> getRoleByName(String name, String application);

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
    Mono<Role> insertRole(Role role);

    /**
     * Delete a role
     * @param roleId the role ID
     * @return an empty result
     */
    Mono<Void> deleteRole(long roleId);

    // TODO method for retrieving all roles for an application
}
