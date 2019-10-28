package net.plshark.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import reactor.util.annotation.Nullable;

/**
 * Data for a group
 */
@AutoValue
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Group {

    public static Group create(String name) {
        return create(null, name);
    }

    @JsonCreator
    public static Group create(@Nullable @JsonProperty("id") Long id, @JsonProperty("name") String name) {
        return new AutoValue_Group(id, name);
    }

    @Nullable
    public abstract Long getId();

    public abstract String getName();
}
