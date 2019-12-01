package net.plshark.users.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

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
    @Nullable @JsonProperty("application_id")
    private final Long applicationId;
    @Nonnull
    private final String name;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoleBuilder {

    }
}
