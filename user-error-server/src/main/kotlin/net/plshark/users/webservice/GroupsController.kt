package net.plshark.users.webservice

import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Group
import net.plshark.users.model.GroupCreate
import net.plshark.users.model.Role
import net.plshark.users.repo.GroupRolesRepository
import net.plshark.users.repo.GroupsRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Controller to provide web service methods for groups
 */
@RestController
@RequestMapping("/groups")
class GroupsController(private val groupsRepo: GroupsRepository, private val groupRolesRepo: GroupRolesRepository) {

    @GetMapping(path = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(@PathVariable("id") id: Long): Mono<Group> {
        return groupsRepo.findById(id)
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No group found for $id") })
    }

    // TODO getGroups

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(@RequestBody group: GroupCreate): Mono<Group> {
        return groupsRepo.insert(group)
            .onErrorMap(DataIntegrityViolationException::class.java) { e: DataIntegrityViolationException? ->
                DuplicateException("A group with name ${group.name} already exists", e)
            }
    }

    @DeleteMapping(path = ["/{id}"])
    fun delete(@PathVariable("id") id: Long): Mono<Void> {
        return groupsRepo.deleteById(id)
    }

    @PostMapping(path = ["/{id}/roles"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addRoleToGroup(@PathVariable("id") groupId: Long, roleId: Long): Mono<Void> {
        // TODO return 404 if group or role not found
        return groupRolesRepo.insert(groupId, roleId)
    }

    @DeleteMapping(path = ["/{id}/roles/{roleId}"])
    fun removeRoleFromGroup(
        @PathVariable("id") groupId: Long,
        @PathVariable("roleId") roleId: Long
    ): Mono<Void> {
        return groupRolesRepo.deleteById(groupId, roleId)
    }

    @GetMapping(path = ["/{id}/roles"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getRolesInGroup(@PathVariable("id") groupId: Long): Flux<Role> {
        return groupRolesRepo.findRolesForGroup(groupId)
    }
}
