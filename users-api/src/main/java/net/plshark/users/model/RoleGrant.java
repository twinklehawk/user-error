package net.plshark.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import reactor.util.annotation.NonNull;

/**
 * Request to grant a role to a user
 */
@Value
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleGrant {

    /** the application name of the role to grant */
    @NonNull
    private final String application;
    /** the name of the role to grant */
    @NonNull
    private final String role;

    @JsonCreator
    public static RoleGrant create(@JsonProperty String application, @JsonProperty String role) {
        return new RoleGrant(application, role);
    }
}
