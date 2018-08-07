package net.plshark.users.repo;

import net.plshark.users.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository for adding and removing roles for users
 */
public interface UserRolesRepository {

    /**
     * Get all the roles for a user
     * @param userId the user ID
     * @return the roles for that user
     */
    Flux<Role> getRolesForUser(long userId);

    /**
     * Grant a role to a user
     * @param userId the ID of the user to grant the role to
     * @param roleId the ID of the role to grant
     * @return an empty result
     */
    Mono<Void> insertUserRole(long userId, long roleId);

    /**
     * Remove a role to a user
     * @param userId the ID of the user to remove the role from
     * @param roleId the ID of the role to remove
     * @return an empty result
     */
    Mono<Void> deleteUserRole(long userId, long roleId);

    /**
     * Delete all user roles for a user
     * @param userId the user ID
     * @return an empty result
     */
    Mono<Void> deleteUserRolesForUser(long userId);

    /**
     * Delete all user roles for a role
     * @param roleId the role ID
     * @return an empty result
     */
    Mono<Void> deleteUserRolesForRole(long roleId);
}
