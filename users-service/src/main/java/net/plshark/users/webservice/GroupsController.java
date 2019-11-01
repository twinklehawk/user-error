package net.plshark.users.webservice;

import java.util.Objects;
import net.plshark.users.model.Group;
import net.plshark.users.service.GroupsService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Controller to provide web service methods for groups
 */
@RestController
@RequestMapping("/groups")
public class GroupsController {

    private final GroupsService groupsService;

    public GroupsController(GroupsService groupsService) {
        this.groupsService = Objects.requireNonNull(groupsService, "groupsService cannot be null");
    }

    /**
     * Get a group by name
     * @param name the name of the group
     * @return the matching group if found
     */
    @GetMapping(path = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Group> get(@PathVariable("name") String name) {
        return groupsService.get(name);
    }

    /**
     * Insert a new group
     * @param group the group
     * @return the inserted group
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Group> insert(@RequestBody Group group) {
        return groupsService.insert(group);
    }

    /**
     * Delete a group
     * @param name the group name
     * @return an empty result
     */
    @DeleteMapping(path = "/{name}")
    public Mono<Void> delete(@PathVariable("name") String name) {
        return groupsService.delete(name);
    }
}
