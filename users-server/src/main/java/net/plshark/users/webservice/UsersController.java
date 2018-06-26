package net.plshark.users.webservice;

import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.plshark.BadRequestException;
import net.plshark.users.PasswordChangeRequest;
import net.plshark.users.User;
import net.plshark.users.service.UserManagementService;
import reactor.core.publisher.Mono;

/**
 * Controller providing web service methods for users
 */
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserManagementService userMgmtService;

    /**
     * Create a new instance
     * @param userManagementService the service to use to modify users
     */
    public UsersController(UserManagementService userManagementService) {
        this.userMgmtService = Objects.requireNonNull(userManagementService, "userManagementService cannot be null");
    }

    /**
     * Insert a new user
     * @param user the user to insert
     * @return the inserted user
     * @throws BadRequestException if attempting to insert a user with an ID already set
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> insert(@RequestBody User user) throws BadRequestException {
        if (user.getId().isPresent())
            throw new BadRequestException("Cannot insert user with ID already set");

        return userMgmtService.saveUser(user)
             // don't send back the password
            .map(savedUser -> new User(savedUser.getId().get(), savedUser.getUsername(), null));
    }

    /**
     * Delete a user by ID
     * @param id the user ID
     * @return an empty result
     */
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable("id") long id) {
        return userMgmtService.deleteUser(id);
    }

    /**
     * Change a user's password
     * @param userId the ID of the user
     * @param request the password change request
     * @return an empty result or ObjectNotFoundException if the user was not found or the current password was incorrect
     */
    @PostMapping(path = "/{id}/password")
    public Mono<Void> changePassword(@PathVariable("id") long userId, @RequestBody PasswordChangeRequest request) {
        return userMgmtService.updateUserPassword(userId, request.getCurrentPassword(), request.getNewPassword());
    }

    /**
     * Grant a role to a user
     * @param userId the ID of the user to grant to
     * @param roleId the ID of the role to grant
     * @return an empty result or ObjectNotFoundException if the user or role does not exist
     */
    @PostMapping(path = "/{userId}/roles/{roleId}")
    public Mono<Void> grantRole(@PathVariable("userId") long userId, @PathVariable("roleId") long roleId) {
        return userMgmtService.grantRoleToUser(userId, roleId);
    }

    /**
     * Remove a role from a user
     * @param userId the ID of the user to remove the role from
     * @param roleId the ID of the role to remove
     * @return an empty result or ObjectNotFoundException if the user does not exist
     */
    @DeleteMapping(path = "/{userId}/roles/{roleId}")
    public Mono<Void> removeRole(@PathVariable("userId") long userId, @PathVariable("roleId") long roleId) {
        return userMgmtService.removeRoleFromUser(userId, roleId);
    }
}
