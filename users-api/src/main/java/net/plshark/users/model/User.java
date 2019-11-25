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
    @NonNull
    private final String username;
    /** the password, must not be null when creating a user, will otherwise be null except unless specifically requested */
    @Nullable
    private final String password;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserBuilder {

    }
}
