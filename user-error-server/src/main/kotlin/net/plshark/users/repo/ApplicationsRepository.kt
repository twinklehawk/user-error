package net.plshark.users.repo

import net.plshark.users.model.Application
import net.plshark.users.model.ApplicationCreate

/**
 * Repository for saving, deleting, and retrieving applications
 */
interface ApplicationsRepository {

    /**
     * Find an application by ID
     * @param id the ID
     * @return the matching application or null if not found
     */
    suspend fun findById(id: Long): Application?

    /**
     * Find an application by name
     * @param name the applications name
     * @return the matching application or null if not found
     */
    suspend fun findByName(name: String): Application?

    /**
     * Insert a new application
     * @param application the application to insert
     * @return the inserted application
     */
    suspend fun insert(application: ApplicationCreate): Application

    /**
     * Delete an application by ID
     * @param id the application ID
     */
    suspend fun deleteById(id: Long)
}
