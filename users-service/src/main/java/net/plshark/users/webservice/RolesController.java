package net.plshark.users.webservice;

import java.util.Objects;
import javax.validation.constraints.Min;
import net.plshark.users.model.Role;
import net.plshark.users.service.RolesService;
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
 * Controller to provide web service methods for roles
 */
@RestController
@RequestMapping("/applications/{application}/roles")
public class RolesController {

    private final RolesService rolesService;

    public RolesController(RolesService rolesService) {
        this.rolesService = Objects.requireNonNull(rolesService, "rolesService cannot be null");
    }

    /**
     * Insert a new role
     * @param application the application the role will belong to
     * @param role the role to insert
     * @return the inserted role
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Role> insert(@PathVariable("application") String application, @RequestBody Role role) {
        return rolesService.insert(application, role);
    }

    /**
     * Delete a role
     * @param application the name of the parent application
     * @param name the role name
     * @return an empty result
     */
    @DeleteMapping(path = "/{name}")
    public Mono<Void> delete(@PathVariable("application") String application, @PathVariable("name") String name) {
        return rolesService.delete(application, name);
    }

    /**
     * Get all roles up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at
     * @return the roles
     */
    // TODO should only get roles belonging to application
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Role> getRoles(@RequestParam(value = "max-results", defaultValue = "50") @Min(1) int maxResults,
                               @RequestParam(value = "offset", defaultValue = "0") @Min(0) long offset) {
        return rolesService.getRoles(maxResults, offset);
    }

    /**
     * Get a role
     * @param application the parent application name
     * @param name the name of the role
     * @return the matching role if found
     */
    @GetMapping(path = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Role> get(@PathVariable("application") String application, @PathVariable("name") String name) {
        return rolesService.get(application, name);
    }
}
