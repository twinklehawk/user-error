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
class RolesServiceImpl(private val rolesRepo: RolesRepository) : RolesService {

    override fun create(role: RoleCreate): Mono<Role> {
        return rolesRepo.insert(role)
            .onErrorMap(DataIntegrityViolationException::class.java) { e: DataIntegrityViolationException? ->
                DuplicateException("A role with name ${role.name} already exists", e)
            }
    }

    override fun delete(roleId: Long): Mono<Void> {
        return rolesRepo.delete(roleId)
    }

    override fun findById(roleId: Long): Mono<Role> {
        return rolesRepo.findById(roleId)
    }

    override fun findRequiredById(roleId: Long): Mono<Role> {
        return findById(roleId)
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No role found for $roleId") })
    }

    override fun getRoles(maxResults: Int, offset: Long): Flux<Role> {
        return rolesRepo.getRoles(maxResults, offset)
    }
}
