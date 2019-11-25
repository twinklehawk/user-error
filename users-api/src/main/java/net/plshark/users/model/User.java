package net.plshark.users.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

/**
 * Data for a user
 */
@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = User.UserBuilder.class)
public class User {

    /** the ID, null if the user has not been saved yet */
    @Nullable
    private final Long id;
    /** the username */
    @Nonnull
    private final String username;
    /** the password, must not be null when creating a user, will otherwise be null except unless specifically requested */
    @Nullable
    private final String password;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserBuilder {

    }
}
