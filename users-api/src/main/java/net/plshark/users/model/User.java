package net.plshark.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import reactor.util.annotation.Nullable;

/**
 * Data for a user
 */
@AutoValue
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class User {

    /**
     * Create a new instance
     * @param username the username
     * @param password the password
     */
    public static User create(String username, String password) {
        return create(null, username, password);
    }

    /**
     * Create a new instance
     * @param id the user ID, can be null if not saved yet
     * @param username the username
     * @param password the password
     */
    @JsonCreator
    public static User create(@Nullable @JsonProperty("id") Long id, @JsonProperty("username") String username,
                              @JsonProperty("password") String password) {
        return new AutoValue_User(id, username, password);
    }

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
     * @return the password
     */
    public abstract String getPassword();
}
