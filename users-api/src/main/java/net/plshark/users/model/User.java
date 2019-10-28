package net.plshark.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import reactor.util.annotation.Nullable;

/**
 * Data for a user
 */
@AutoValue
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = AutoValue_User.Builder.class)
public abstract class User {

    /**
     * @return the ID, null if the user has not been saved yet
     */
    @Nullable
    public abstract Long getId();

    /**
     * @return the username
     */
    public abstract String getUsername();

    /**
     * @return the password, must not be null when creating a user, will otherwise be null except unless specifically
     * requested
     */
    @Nullable
    public abstract String getPassword();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_User.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract static class Builder {

        public abstract Builder id(Long id);

        public abstract Builder username(String username);

        public abstract Builder password(String password);

        public abstract User build();
    }
}
