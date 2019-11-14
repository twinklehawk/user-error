package net.plshark.users.webservice;

import java.util.Objects;
import javax.validation.constraints.Min;
import net.plshark.errors.BadRequestException;
import net.plshark.errors.ObjectNotFoundException;
import net.plshark.users.model.PasswordChangeRequest;
import net.plshark.users.model.RoleGrant;
import net.plshark.users.model.User;
import net.plshark.users.service.UsersService;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
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
        return usersService.get(username)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ObjectNotFoundException("No user found for username"))));
    }

    /**
     * Insert a new user
     * @param user the user to insert
     * @return the inserted user
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> create(@RequestBody User user) {
        if (!StringUtils.hasLength(user.getPassword()))
            throw new BadRequestException("Password cannot be empty");
        return usersService.create(user);
    }

    /**
     * Delete a user by username
     * @param username the username
     * @return an empty result
     */
    @DeleteMapping("/{username}")
    public Mono<Void> delete(@PathVariable("username") String username) {
        return usersService.delete(username);
    }

    /**
     * Change a user's password
     * @param username the username of the user
     * @param request the password change request
     * @return an empty result or ObjectNotFoundException if the user was not found or the current password was incorrect
     */
    @PostMapping(path = "/{username}/password")
    public Mono<Void> changePassword(@PathVariable("username") String username, @RequestBody PasswordChangeRequest request) {
        return usersService.updateUserPassword(username, request.getCurrentPassword(), request.getNewPassword());
    }

    /**
     * Grant a role to a user
     * @param username the username of the user to grant to
     * @param roleGrant the role to grant
     * @return an empty result or ObjectNotFoundException if the user or role does not exist
     */
    @PostMapping(path = "/{username}/roles")
    public Mono<Void> grantRole(@PathVariable("username") String username, @RequestBody RoleGrant roleGrant) {
        return usersService.grantRoleToUser(username, roleGrant.getApplication(), roleGrant.getRole());
    }

    /**
     * Remove a role from a user
     * @param username the name of the user to remove the role from
     * @param role the name of the role to remove
     * @return an empty result or ObjectNotFoundException if the user does not exist
     */
    @DeleteMapping(path = "/{username}/roles/{application}/{role}")
    public Mono<Void> removeRole(@PathVariable("username") String username, @PathVariable("application") String application,
                                 @PathVariable("role") String role) {
        return usersService.removeRoleFromUser(username, application, role);
    }

    /**
     * Grant a group to a user
     * @param username the username of the user to grant to
     * @param group the name of the group to grant
     * @return an empty result or ObjectNotFoundException if the user or group does not exist
     */
    @PostMapping(path = "/{username}/groups/{group}")
    public Mono<Void> grantGroup(@PathVariable("username") String username, @PathVariable("group") String group) {
        return usersService.grantGroupToUser(username, group);
    }

    /**
     * Remove a group from a user
     * @param username the name of the user to remove the role from
     * @param group the name of the group to remove
     * @return an empty result or ObjectNotFoundException if the user does not exist
     */
    @DeleteMapping(path = "/{username}/groups/{group}")
    public Mono<Void> removeGroup(@PathVariable("username") String username, @PathVariable("group") String group) {
        return usersService.removeGroupFromUser(username, group);
    }
}
