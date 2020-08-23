package net.plshark.users.webservice

import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Application
import net.plshark.users.model.ApplicationCreate
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

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getApplications(
        @RequestParam(value = "limit", defaultValue = "50") limit: @Min(1) Int,
        @RequestParam(value = "offset", defaultValue = "0") offset: @Min(0) Long
    ): Flux<Application> {
        return applicationsService.getApplications(limit, offset)
    }

    @GetMapping(path = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(@PathVariable("id") id: Long): Mono<Application> {
        return applicationsService.findById(id)
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No application found for $id") })
    }

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(@RequestBody application: ApplicationCreate): Mono<Application> {
        return applicationsService.create(application)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): Mono<Void> {
        return applicationsService.deleteById(id)
    }
}
