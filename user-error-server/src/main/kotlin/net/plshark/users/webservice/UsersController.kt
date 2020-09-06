package net.plshark.users.webservice

import net.plshark.errors.BadRequestException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Group
import net.plshark.users.model.PasswordChangeRequest
import net.plshark.users.model.Role
import net.plshark.users.model.User
import net.plshark.users.model.UserCreate
import net.plshark.users.service.UsersService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.constraints.Min

/**
 * Controller providing web service methods for users
 */
@RestController
@RequestMapping("/users")
class UsersController(private val usersService: UsersService) {

    /**
     * Get all users up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at
     * @return the users
     */
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUsers(
        @RequestParam(value = "max-results", defaultValue = "50") maxResults: @Min(1) Int,
        @RequestParam(value = "offset", defaultValue = "0") offset: @Min(0) Long
    ): Flux<User> {
        return usersService.getUsers(maxResults, offset)
    }

    /**
     * Retrieve a user by ID
     * @param id the user ID
     * @return the matching user
     */
    @GetMapping(path = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(@PathVariable("id") id: Long): Mono<User> {
        return usersService.findById(id)
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No user found for id") })
    }

    /**
     * Insert a new user
     * @param user the user to insert
     * @return the inserted user
     */
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody user: UserCreate): Mono<User> {
        if (user.password.isEmpty()) throw BadRequestException("Password cannot be empty")
        return usersService.create(user)
    }

    /**
     * Delete a user by ID
     * @param id the ID
     * @return an empty result
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): Mono<Void> {
        return usersService.deleteById(id)
    }

    /**
     * Change a user's password
     * @param id the user ID
     * @param request the password change request
     * @return an empty result or ObjectNotFoundException if the user was not found or the current password was
     * incorrect
     */
    @PostMapping(path = ["/{id}/password"])
    fun changePassword(
        @PathVariable("id") id: Long,
        @RequestBody request: PasswordChangeRequest
    ): Mono<Void> {
        return usersService.updateUserPassword(id, request.currentPassword, request.newPassword)
    }

    @GetMapping(path = ["/{id}/roles"])
    fun getUserRoles(@PathVariable("id") id: Long): Flux<Role> {
        return usersService.getUserRoles(id)
    }

    @PutMapping(path = ["/{id}/roles"])
    fun updateUserRoles(@PathVariable("id") id: Long, @RequestBody updatedRoles: Set<Role>): Flux<Role> {
        return usersService.updateUserRoles(id, updatedRoles)
    }

    @GetMapping(path = ["/{id}/groups"])
    fun getUserGroups(@PathVariable("id") id: Long): Flux<Group> {
        return usersService.getUserGroups(id)
    }

    @PutMapping(path = ["/{id}/groups"])
    fun updateUserGroups(@PathVariable("id") id: Long, @RequestBody updatedGroups: Set<Group>): Flux<Group> {
        return usersService.updateUserGroups(id, updatedGroups)
    }
}
