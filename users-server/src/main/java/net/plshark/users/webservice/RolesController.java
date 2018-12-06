package net.plshark.users.webservice;

import java.util.Objects;

import javax.validation.constraints.Min;
import net.plshark.users.model.Role;
import net.plshark.users.service.UserManagementService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.plshark.BadRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller to provide web service methods for roles
 */
@RestController
@RequestMapping("/roles")
public class RolesController {

    private final UserManagementService userMgmtService;

    /**
     * Create a new instance
     * @param userMgmtService the service to use to create, delete, and modify roles
     */
    public RolesController(UserManagementService userMgmtService) {
        this.userMgmtService = Objects.requireNonNull(userMgmtService, "userMgmtService cannot be null");
    }

    /**
     * Insert a new role
     * @param role the role to insert
     * @return the inserted role
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Role> insert(@RequestBody Role role) {
        return userMgmtService.insertRole(role);
    }

    /**
     * Delete a role by ID
     * @param id the role ID
     * @return an empty result
     */
    @DeleteMapping(path = "/{id}")
    public Mono<Void> delete(@PathVariable("id") long id) {
        return userMgmtService.deleteRole(id);
    }

    /**
     * Get all roles up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at
     * @return the roles
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Role> getRoles(@RequestParam(value = "max-results", defaultValue = "50") @Min(1) int maxResults,
                               @RequestParam(value = "offset", defaultValue = "0") @Min(0) long offset) {
        return userMgmtService.getRoles(maxResults, offset);
    }

    /**
     * Get a role by name
     * @param name the name of the role
     * @return the matching role if found
     */
    @GetMapping(path = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Role> getByName(String name) {
        return userMgmtService.getRoleByName(name);
    }
}
