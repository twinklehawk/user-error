package net.plshark.usererror.user

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for managing applications
 */
interface ApplicationsService {

    /**
     * Find an application by ID
     * @param id the application ID
     * @return a [Mono] emitting the matching application or empty if not found
     */
    fun findById(id: Long): Mono<Application>

    /**
     * Get all applications up to the maximum result count and starting at an offset
     * @param limit the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return a [Flux] emitting the applications
     */
    fun getApplications(limit: Int, offset: Long): Flux<Application>

    /**
     * Save a new application
     * @param application the application
     * @return a [Mono] emitting the saved application or a [net.plshark.errors.DuplicateException] if an application
     * with the same name already exists
     */
    fun create(application: ApplicationCreate): Mono<Application>

    /**
     * Delete an application by ID
     * @param id the application ID
     * @return a [Mono] signalling when complete
     */
    fun deleteById(id: Long): Mono<Void>
}
