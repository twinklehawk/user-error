package net.plshark.users.repo

import net.plshark.users.model.Application
import net.plshark.users.model.ApplicationCreate
import reactor.core.publisher.Mono

/**
 * Repository for saving, deleting, and retrieving applications
 */
interface ApplicationsRepository {

    /**
     * Get an application by ID
     * @param id the ID
     * @return the matching application or empty if not found
     */
    fun findById(id: Long): Mono<Application>

    /**
     * Get an application by name
     * @param name the applications name
     * @return the matching application or empty if not found
     */
    fun findByName(name: String): Mono<Application>

    /**
     * Insert a new application
     * @param application the application to insert
     * @return the inserted application, will have the ID set
     */
    fun insert(application: ApplicationCreate): Mono<Application>

    /**
     * Delete an application by ID
     * @param id the application ID
     * @return an empty result
     */
    fun deleteById(id: Long): Mono<Void>
}
