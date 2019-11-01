package net.plshark.users.repo;

import net.plshark.users.model.Application;
import reactor.core.publisher.Mono;

/**
 * Repository for saving, deleting, and retrieving applications
 */
public interface ApplicationsRepository {

    /**
     * Get an application by ID
     * @param id the ID
     * @return the matching application or empty if not found
     */
    Mono<Application> get(long id);

    /**
     * Get an application by name
     * @param name the applications name
     * @return the matching application or empty if not found
     */
    Mono<Application> get(String name);

    /**
     * Insert a new application
     * @param application the application to insert
     * @return the inserted application, will have the ID set
     */
    Mono<Application> insert(Application application);

    /**
     * Delete an application by ID
     * @param id the application ID
     * @return an empty result
     */
    Mono<Void> delete(long id);

    /**
     * Delete an application by name
     * @param name the application name
     * @return an empty result
     */
    Mono<Void> delete(String name);
}
