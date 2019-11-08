package net.plshark.users.webservice;

import java.util.Objects;
import javax.validation.constraints.Min;
import net.plshark.errors.ObjectNotFoundException;
import net.plshark.users.model.Application;
import net.plshark.users.service.ApplicationsService;
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
 * Controller providing web service methods for applications
 */
@RestController
@RequestMapping("/applications")
public class ApplicationsController {

    private final ApplicationsService applicationsService;

    public ApplicationsController(ApplicationsService applicationsService) {
        this.applicationsService = Objects.requireNonNull(applicationsService);
    }

    /**
     * Get all applications up to the maximum result count and starting at an offset
     * @param limit the maximum number of results to return
     * @param offset the offset to start the list at
     * @return the users
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Application> getApplications(@RequestParam(value = "limit", defaultValue = "50") @Min(1) int limit,
                                             @RequestParam(value = "offset", defaultValue = "0") @Min(0) long offset) {
        return applicationsService.getApplications(limit, offset);
    }

    /**
     * Retrieve an application
     * @param name the application name
     * @return the application
     */
    @GetMapping(path = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Application> get(@PathVariable("name") String name) {
        return applicationsService.get(name)
                .switchIfEmpty(Mono.error(() -> new ObjectNotFoundException("No application found for " + name)));
    }

    /**
     * Insert a new application
     * @param application the application
     * @return the inserted application
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Application> create(@RequestBody Application application) {
        // TODO verify DB constraint violations turn into conflict response status code
        return applicationsService.create(application);
    }

    /**
     * Delete an application
     * @param name the application name
     * @return when complete
     */
    @DeleteMapping("/{name}")
    public Mono<Void> delete(@PathVariable("name") String name) {
        return applicationsService.delete(name);
    }
}
