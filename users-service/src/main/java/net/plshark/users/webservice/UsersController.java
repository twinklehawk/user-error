package net.plshark.users.webservice;

import java.util.Objects;
import javax.validation.constraints.Min;
import net.plshark.errors.ObjectNotFoundException;
import net.plshark.users.model.PasswordChangeRequest;
import net.plshark.users.model.User;
import net.plshark.users.service.UsersService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller providing web service methods for users
 */
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    /**
     * Create a new instance
     * @param usersService the service to use to modify users
     */
    public UsersController(UsersService usersService) {
        this.usersService = Objects.requireNonNull(usersService, "usersService cannot be null");
    }

    /**
     * Get all users up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at
     * @return the users
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<User> getUsers(@RequestParam(value = "max-results", defaultValue = "50") @Min(1) int maxResults,
                                   @RequestParam(value = "offset", defaultValue = "0") @Min(0) long offset) {
        return usersService.getUsers(maxResults, offset);
    }

    /**
     * Retrieve a user by username
     * @param username the username
     * @return the matching user
     */
    @GetMapping(path = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> getUser(@PathVariable("username") String username) {
        return usersService.getUserByUsername(username)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ObjectNotFoundException("No user found for username"))));
    }

    /**
     * Insert a new user
     * @param user the user to insert
     * @return the inserted user
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> insert(@RequestBody User user) {
        return usersService.insertUser(user);
    }

    /**
     * Delete a user by username
     * @param username the username
     * @return an empty result
     */
    @DeleteMapping("/{username}")
    public Mono<Void> delete(@PathVariable("username") String username) {
        return usersService.deleteUser(username);
    }

    /**
     * Change a user's password
     * @param userId the ID of the user
     * @param request the password change request
     * @return an empty result or ObjectNotFoundException if the user was not found or the current password was incorrect
     */
    @PostMapping(path = "/{id}/password")
    public Mono<Void> changePassword(@PathVariable("id") long userId, @RequestBody PasswordChangeRequest request) {
        return usersService.updateUserPassword(userId, request.getCurrentPassword(), request.getNewPassword());
    }

    /**
     * Grant a role to a user
     * @param userId the ID of the user to grant to
     * @param roleId the ID of the role to grant
     * @return an empty result or ObjectNotFoundException if the user or role does not exist
     */
    @PostMapping(path = "/{userId}/roles/{roleId}")
    public Mono<Void> grantRole(@PathVariable("userId") long userId, @PathVariable("roleId") long roleId) {
        return usersService.grantRoleToUser(userId, roleId);
    }

    /**
     * Remove a role from a user
     * @param userId the ID of the user to remove the role from
     * @param roleId the ID of the role to remove
     * @return an empty result or ObjectNotFoundException if the user does not exist
     */
    @DeleteMapping(path = "/{userId}/roles/{roleId}")
    public Mono<Void> removeRole(@PathVariable("userId") long userId, @PathVariable("roleId") long roleId) {
        return usersService.removeRoleFromUser(userId, roleId);
    }
}
