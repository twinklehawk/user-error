package net.plshark.users.repo

import net.plshark.users.model.Application
import net.plshark.users.model.ApplicationCreate
import reactor.core.publisher.Mono

/**
 * Repository for saving, deleting, and retrieving applications
 */
interface ApplicationsRepository {

    /**
     * Find an application by ID
     * @param id the ID
     * @return a [Mono] emitting the matching application or empty if not found
     */
    fun findById(id: Long): Mono<Application>

    /**
     * Find an application by name
     * @param name the applications name
     * @return a [Mono] emitting the matching application or empty if not found
     */
    fun findByName(name: String): Mono<Application>

    /**
     * Insert a new application
     * @param application the application to insert
     * @return a [Mono] emitting the inserted application
     */
    fun insert(application: ApplicationCreate): Mono<Application>

    /**
     * Delete an application by ID
     * @param id the application ID
     * @return a [Mono] signalling when complete
     */
    fun deleteById(id: Long): Mono<Void>
}
