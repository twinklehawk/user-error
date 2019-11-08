package net.plshark.users.service;

import net.plshark.users.model.Application;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service for managing applications
 */
public interface ApplicationsService {

    /**
     * Retrieve an application by name
     * @param name the application name
     * @return the matching application or empty if not found
     */
    Mono<Application> get(String name);

    /**
     * Get all applications up to the maximum result count and starting at an offset
     * @param limit the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return the applications
     */
    Flux<Application> getApplications(int limit, long offset);

    /**
     * Save a new application
     * @param application the application
     * @return the saved application
     */
    Mono<Application> create(Application application);

    /**
     * Delete an application
     * @param name the application name
     * @return an empty result
     */
    Mono<Void> delete(String name);
}
