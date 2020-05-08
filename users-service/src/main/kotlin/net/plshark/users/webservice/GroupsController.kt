package net.plshark.users.webservice

import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Group
import net.plshark.users.service.GroupsService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Controller to provide web service methods for groups
 */
@RestController
@RequestMapping("/groups")
class GroupsController(private val groupsService: GroupsService) {

    /**
     * Get a group by name
     * @param name the name of the group
     * @return the matching group if found
     */
    @GetMapping(path = ["/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    operator fun get(@PathVariable("name") name: String): Mono<Group> {
        return groupsService[name]
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No group found for $name") })
    }

    /**
     * Insert a new group
     * @param group the group
     * @return the inserted group
     */
    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(@RequestBody group: Group): Mono<Group> {
        return groupsService.create(group)
    }

    /**
     * Delete a group
     * @param name the group name
     * @return an empty result
     */
    @DeleteMapping(path = ["/{name}"])
    fun delete(@PathVariable("name") name: String): Mono<Void> {
        return groupsService.delete(name)
    }

    // TODO methods for adding/removing roles from group, viewing roles in group

}
