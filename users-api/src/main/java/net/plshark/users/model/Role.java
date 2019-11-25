package net.plshark.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

/**
 * Data for a role
 */
@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = Role.RoleBuilder.class)
public class Role {

    /** the ID, can be null if not saved yet */
    @Nullable
    private final Long id;
    /** the application ID, can be null if not saved yet and inserting using the application name */
    @Nullable
    private final Long applicationId;
    @NonNull
    private final String name;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoleBuilder {

    }
}
