package net.plshark.users.webservice

import net.plshark.errors.BadRequestException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.PasswordChangeRequest
import net.plshark.users.model.RoleGrant
import net.plshark.users.model.User
import net.plshark.users.service.UsersService
import org.springframework.http.MediaType
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
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
     * Retrieve a user by username
     * @param username the username
     * @return the matching user
     */
    @GetMapping(path = ["/{username}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUser(@PathVariable("username") username: String): Mono<User> {
        return usersService.get(username)
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No user found for username") })
    }

    /**
     * Insert a new user
     * @param user the user to insert
     * @return the inserted user
     */
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody user: User): Mono<User> {
        if (!StringUtils.hasLength(user.password)) throw BadRequestException("Password cannot be empty")
        return usersService.create(user)
    }

    /**
     * Delete a user by username
     * @param username the username
     * @return an empty result
     */
    @DeleteMapping("/{username}")
    fun delete(@PathVariable("username") username: String): Mono<Void> {
        return usersService.delete(username)
    }

    /**
     * Change a user's password
     * @param username the username of the user
     * @param request the password change request
     * @return an empty result or ObjectNotFoundException if the user was not found or the current password was incorrect
     */
    @PostMapping(path = ["/{username}/password"])
    fun changePassword(
        @PathVariable("username") username: String,
        @RequestBody request: PasswordChangeRequest
    ): Mono<Void> {
        return usersService.updateUserPassword(username, request.currentPassword, request.newPassword)
    }

    /**
     * Grant a role to a user
     * @param username the username of the user to grant to
     * @param roleGrant the role to grant
     * @return an empty result or ObjectNotFoundException if the user or role does not exist
     */
    @PostMapping(path = ["/{username}/roles"])
    fun grantRole(
        @PathVariable("username") username: String,
        @RequestBody roleGrant: RoleGrant): Mono<Void> {
        return usersService.grantRoleToUser(username, roleGrant.application, roleGrant.role)
    }

    /**
     * Remove a role from a user
     * @param username the name of the user to remove the role from
     * @param role the name of the role to remove
     * @return an empty result or ObjectNotFoundException if the user does not exist
     */
    @DeleteMapping(path = ["/{username}/roles/{application}/{role}"])
    fun removeRole(
        @PathVariable("username") username: String,
        @PathVariable("application") application: String,
        @PathVariable("role") role: String
    ): Mono<Void> {
        return usersService.removeRoleFromUser(username, application, role)
    }

    /**
     * Grant a group to a user
     * @param username the username of the user to grant to
     * @param group the name of the group to grant
     * @return an empty result or ObjectNotFoundException if the user or group does not exist
     */
    @PostMapping(path = ["/{username}/groups/{group}"])
    fun grantGroup(
        @PathVariable("username") username: String,
        @PathVariable("group") group: String): Mono<Void> {
        return usersService.grantGroupToUser(username, group)
    }

    /**
     * Remove a group from a user
     * @param username the name of the user to remove the role from
     * @param group the name of the group to remove
     * @return an empty result or ObjectNotFoundException if the user does not exist
     */
    @DeleteMapping(path = ["/{username}/groups/{group}"])
    fun removeGroup(
        @PathVariable("username") username: String,
        @PathVariable("group") group: String): Mono<Void> {
        return usersService.removeGroupFromUser(username, group)
    }

}
