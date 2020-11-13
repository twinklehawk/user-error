package net.plshark.users.webservice

import kotlinx.coroutines.flow.Flow
import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Application
import net.plshark.users.model.ApplicationCreate
import net.plshark.users.repo.ApplicationsRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Min

/**
 * Controller providing web service methods for applications
 */
@RestController
@RequestMapping("/applications")
class ApplicationsController(private val appsRepo: ApplicationsRepository) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getApplications(
        @RequestParam(value = "limit", defaultValue = "50") limit: @Min(1) Int,
        @RequestParam(value = "offset", defaultValue = "0") offset: @Min(0) Int
    ): Flow<Application> {
        return appsRepo.getAll(limit, offset)
    }

    @GetMapping(path = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findById(@PathVariable("id") id: Long): Application {
        return appsRepo.findById(id) ?: throw ObjectNotFoundException("No application found for $id")
    }

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun create(@RequestBody application: ApplicationCreate): Application {
        try {
            return appsRepo.insert(application)
        } catch (e: DataIntegrityViolationException) {
            throw DuplicateException("An application with name ${application.name} already exists", e)
        }
    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable("id") id: Long) {
        appsRepo.deleteById(id)
    }
}
