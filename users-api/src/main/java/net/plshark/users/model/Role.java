package net.plshark.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import reactor.util.annotation.Nullable;

/**
 * Data for a role
 */
@AutoValue
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = AutoValue_Role.Builder.class)
public abstract class Role {

    public static Builder builder() {
        return new AutoValue_Role.Builder();
    }

    /**
     * @return the ID, can be null if not saved yet
     */
    @Nullable
    public abstract Long getId();

    @Nullable
    @JsonProperty("application_id")
    public abstract Long getApplicationId();

    /**
     * @return the role name
     */
    public abstract String getName();

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract static class Builder {

        public abstract Builder id(Long id);

        @JsonProperty("application_id")
        public abstract Builder applicationId(Long applicationId);

        public abstract Builder name(String name);

        public abstract Role build();
    }
}
