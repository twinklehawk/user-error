package net.plshark.users.webservice

import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Application
import net.plshark.users.service.ApplicationsService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.constraints.Min

/**
 * Controller providing web service methods for applications
 */
@RestController
@RequestMapping("/applications")
class ApplicationsController(private val applicationsService: ApplicationsService) {

    /**
     * Get all applications up to the maximum result count and starting at an offset
     * @param limit the maximum number of results to return
     * @param offset the offset to start the list at
     * @return the users
     */
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getApplications(
        @RequestParam(value = "limit", defaultValue = "50") limit: @Min(1) Int,
        @RequestParam(value = "offset", defaultValue = "0") offset: @Min(0) Long
    ): Flux<Application> {
        return applicationsService.getApplications(limit, offset)
    }

    /**
     * Retrieve an application
     * @param name the application name
     * @return the application
     */
    @GetMapping(path = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    operator fun get(@PathVariable("name") name: String): Mono<Application> {
        return applicationsService.get(name)
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No application found for $name") })
    }

    /**
     * Insert a new application
     * @param application the application
     * @return the inserted application
     */
    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(@RequestBody application: Application): Mono<Application> {
        return applicationsService.create(application)
    }

    /**
     * Delete an application
     * @param name the application name
     * @return when complete
     */
    @DeleteMapping("/{name}")
    fun delete(@PathVariable("name") name: String): Mono<Void> {
        return applicationsService.delete(name)
    }
}