package net.plshark.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import reactor.util.annotation.Nullable;

/**
 * Data for an application
 */
@AutoValue
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = AutoValue_Application.Builder.class)
public abstract class Application {

    public static Builder builder() {
        return new AutoValue_Application.Builder();
    }

    /**
     * @return the ID, can be null if not saved yet
     */
    @Nullable
    public abstract Long getId();

    /**
     * @return the application name
     */
    public abstract String getName();

    /**
     * @return a new builder initialized with the values of this application
     */
    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract static class Builder {

        public abstract Builder id(Long id);

        public abstract Builder name(String name);

        public abstract Application build();
    }
}
