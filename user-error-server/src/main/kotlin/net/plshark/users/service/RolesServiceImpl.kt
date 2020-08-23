package net.plshark.users.service

import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Role
import net.plshark.users.model.RoleCreate
import net.plshark.users.repo.RolesRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Role management service implementation
 */
@Component
class RolesServiceImpl(private val rolesRepo: RolesRepository) :
    RolesService {

    override fun create(role: RoleCreate): Mono<Role> {
        return rolesRepo.insert(role)
            .onErrorMap(DataIntegrityViolationException::class.java) { e: DataIntegrityViolationException? ->
                DuplicateException("A role with name ${role.name} already exists", e)
            }
    }

    override fun delete(roleId: Long): Mono<Void> {
        return rolesRepo.delete(roleId)
    }

    override fun delete(applicationId: Long, name: String): Mono<Void> {
        return get(applicationId, name)
            .flatMap { role: Role -> delete(role.id) }
    }

    override fun get(applicationId: Long, name: String): Mono<Role> {
        return rolesRepo[applicationId, name]
    }

    override fun getRequired(applicationId: Long, name: String): Mono<Role> {
        return get(applicationId, name)
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No role found for $applicationId:$name") })
    }

    override fun getRoles(maxResults: Int, offset: Long): Flux<Role> {
        return rolesRepo.getRoles(maxResults, offset)
    }
}
