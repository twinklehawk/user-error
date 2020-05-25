package net.plshark.users.service

import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Group
import net.plshark.users.model.GroupCreate
import net.plshark.users.repo.GroupRolesRepository
import net.plshark.users.repo.GroupsRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Group management service implementation
 */
@Component
class GroupsServiceImpl(private val groupsRepo: GroupsRepository, private val groupRolesRepo: GroupRolesRepository) : GroupsService {

    override fun get(name: String): Mono<Group> {
        return groupsRepo.getForName(name)
    }

    override fun getRequired(name: String): Mono<Group> {
        return get(name).switchIfEmpty(Mono.error { ObjectNotFoundException("No group found for $name") })
    }

    override fun getGroups(maxResults: Int, offset: Long): Flux<Group> {
        return groupsRepo.getGroups(maxResults, offset)
    }

    override fun create(group: GroupCreate): Mono<Group> {
        return groupsRepo.insert(group)
            .onErrorMap(DataIntegrityViolationException::class.java) { e: DataIntegrityViolationException? ->
                DuplicateException("A group with name ${group.name} already exists", e)
            }
    }

    override fun delete(groupId: Long): Mono<Void> {
        return groupsRepo.delete(groupId)
    }

    override fun delete(name: String): Mono<Void> {
        return get(name)
            .flatMap { group: Group -> delete(group.id) }
    }

    override fun addRoleToGroup(groupId: Long, roleId: Long): Mono<Void> {
        return groupRolesRepo.insert(groupId, roleId)
    }

    override fun removeRoleFromGroup(groupId: Long, roleId: Long): Mono<Void> {
        return groupRolesRepo.delete(groupId, roleId)
    }
}