package net.plshark.users.webservice

import net.plshark.errors.BadRequestException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.PasswordChangeRequest
import net.plshark.users.model.RoleGrant
import net.plshark.users.model.User
import net.plshark.users.model.UserCreate
import net.plshark.users.service.UsersService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
        return usersService.delete(id)
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

    /**
     * Grant a role to a user
     * @param id the ID of the user to grant to
     * @param roleGrant the role to grant
     * @return an empty result or ObjectNotFoundException if the user or role does not exist
     */
    @PostMapping(path = ["/{id}/roles"])
    fun grantRole(
        @PathVariable("id") id: Long,
        @RequestBody roleGrant: RoleGrant
    ): Mono<Void> {
        return usersService.grantRoleToUser(id, roleGrant.applicationId, roleGrant.roleId)
    }

    /**
     * Remove a role from a user
     * @param id the user ID
     * @param applicationId the ID of the parent application
     * @param roleId the ID of the role to remove
     * @return an empty result or ObjectNotFoundException if the user does not exist
     */
    @DeleteMapping(path = ["/{id}/roles/{applicationId}/{roleId}"])
    fun removeRole(
        @PathVariable("id") id: Long,
        @PathVariable("applicationId") applicationId: Long,
        @PathVariable("roleId") roleId: Long
    ): Mono<Void> {
        return usersService.removeRoleFromUser(id, applicationId, roleId)
    }

    /**
     * Grant a group to a user
     * @param id the user ID
     * @param groupId the ID of the group to grant
     * @return an empty result or ObjectNotFoundException if the user or group does not exist
     */
    @PostMapping(path = ["/{id}/groups/{groupId}"])
    fun grantGroup(
        @PathVariable("id") id: Long,
        @PathVariable("groupId") groupId: Long
    ): Mono<Void> {
        return usersService.grantGroupToUser(id, groupId)
    }

    /**
     * Remove a group from a user
     * @param id the user ID
     * @param groupId the ID of the group to remove
     * @return an empty result or ObjectNotFoundException if the user does not exist
     */
    @DeleteMapping(path = ["/{id}/groups/{groupId}"])
    fun removeGroup(
        @PathVariable("id") id: Long,
        @PathVariable("groupId") groupId: Long
    ): Mono<Void> {
        return usersService.removeGroupFromUser(id, groupId)
    }
}
