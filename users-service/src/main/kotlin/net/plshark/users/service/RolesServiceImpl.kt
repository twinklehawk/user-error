package net.plshark.users.service

import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Role
import net.plshark.users.repo.ApplicationsRepository
import net.plshark.users.repo.RolesRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Role management service implementation
 */
@Component
class RolesServiceImpl(private val rolesRepo: RolesRepository, private val appsRepo: ApplicationsRepository) : RolesService {

    override fun create(role: Role): Mono<Role> {
        return rolesRepo.insert(role)
            .onErrorMap(DataIntegrityViolationException::class.java) { e: DataIntegrityViolationException? ->
                DuplicateException("A role with name ${role.name} already exists", e)
            }
    }

    override fun create(application: String, role: Role): Mono<Role> {
        return appsRepo[application]
            // TODO handle no application found
            .flatMap { app -> create(role.copy( applicationId = app.id!!))}
    }

    override fun delete(roleId: Long): Mono<Void> {
        return rolesRepo.delete(roleId)
    }

    override fun delete(application: String, name: String): Mono<Void> {
        return get(application, name)
            .flatMap{ role: Role -> delete(role.id!!) }
    }

    override fun get(application: String, name: String): Mono<Role> {
        return appsRepo[application]
            .flatMap { app -> rolesRepo[app.id!!, name] }
    }

    override fun getRequired(application: String, name: String): Mono<Role> {
        return get(application, name)
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No role found for $application:$name") })
    }

    override fun getRoles(maxResults: Int, offset: Long): Flux<Role> {
        return rolesRepo.getRoles(maxResults, offset)
    }
}
