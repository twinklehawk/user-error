package net.plshark.users.service

import net.plshark.users.model.Application
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for managing applications
 */
interface ApplicationsService {
    /**
     * Retrieve an application by name
     * @param name the application name
     * @return the matching application or empty if not found
     */
    operator fun get(name: String): Mono<Application>

    /**
     * Get all applications up to the maximum result count and starting at an offset
     * @param limit the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return the applications
     */
    fun getApplications(limit: Int, offset: Long): Flux<Application>

    /**
     * Save a new application
     * @param application the application
     * @return the saved application or a [net.plshark.errors.DuplicateException] if an application with the same
     * name already exists
     */
    fun create(application: Application): Mono<Application>

    /**
     * Delete an application
     * @param name the application name
     * @return an empty result
     */
    fun delete(name: String): Mono<Void>
}