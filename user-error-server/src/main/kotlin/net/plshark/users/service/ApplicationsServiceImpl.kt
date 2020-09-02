package net.plshark.users.service

import net.plshark.errors.DuplicateException
import net.plshark.users.model.Application
import net.plshark.users.model.ApplicationCreate
import net.plshark.users.repo.ApplicationsRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Applications management service implementation
 */
@Component
class ApplicationsServiceImpl(private val appsRepo: ApplicationsRepository) :
    ApplicationsService {

    override fun findById(id: Long): Mono<Application> {
        return appsRepo.findById(id)
    }

    override fun getApplications(limit: Int, offset: Long): Flux<Application> {
        // TODO
        return Flux.empty()
    }

    override fun create(application: ApplicationCreate): Mono<Application> {
        return appsRepo.insert(application)
            .onErrorMap(DataIntegrityViolationException::class.java) { e: DataIntegrityViolationException ->
                DuplicateException("An application with name ${application.name} already exists", e)
            }
    }

    override fun deleteById(id: Long): Mono<Void> {
        return appsRepo.deleteById(id)
    }
}
