package net.plshark.users.service

import net.plshark.errors.DuplicateException
import net.plshark.users.model.Application
import net.plshark.users.model.Role
import net.plshark.users.repo.ApplicationsRepository
import net.plshark.users.repo.RolesRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Applications management service implementation
 */
@Component
class ApplicationsServiceImpl(private val appsRepo: ApplicationsRepository, private val rolesRepo: RolesRepository) :
    ApplicationsService {

    override fun get(name: String): Mono<Application> {
        return appsRepo[name]
    }

    override fun getApplications(limit: Int, offset: Long): Flux<Application> {
        // TODO
        return Flux.empty()
    }

    override fun create(application: Application): Mono<Application> {
        return appsRepo.insert(application)
            .onErrorMap(DataIntegrityViolationException::class.java) { e: DataIntegrityViolationException ->
                DuplicateException("An application with name ${application.name} already exists", e)
            }
    }

    override fun delete(name: String): Mono<Void> {
        return get(name)
            .map { application -> application.id!! }
            .flatMap { id -> appsRepo.delete(id) }
    }

    fun getApplicationRoles(id: Long): Flux<Role> {
        return rolesRepo.getRolesForApplication(id)
    }
}
