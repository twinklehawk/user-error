package net.plshark.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import reactor.util.annotation.Nullable;

/**
 * Data for a role
 */
@AutoValue
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Role {

    public static Role create(String name, String application) {
        return create(null, name, application);
    }

    @JsonCreator
    public static Role create(@Nullable @JsonProperty("id") Long id, @JsonProperty("name") String name,
                              @JsonProperty("application") String application) {
        return new AutoValue_Role(id, name, application);
    }

    /**
     * @return the ID, can be null if not saved yet
     */
    @Nullable
    public abstract Long getId();

    /**
     * @return the role name
     */
    public abstract String getName();

    /**
     * @return the application this role belongs to
     */
    public abstract String getApplication();
}
