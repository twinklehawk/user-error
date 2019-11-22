package net.plshark.users.auth.model;

import java.util.Arrays;
import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;

@AutoValue
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AuthenticatedUser {

    @JsonCreator
    public static AuthenticatedUser create(@JsonProperty String username, @JsonProperty ImmutableSet<String> authorities) {
        return new AutoValue_AuthenticatedUser(username, authorities);
    }

    public static AuthenticatedUser create(String username, String... authorities) {
        return create(username, Arrays.asList(authorities));
    }

    public static AuthenticatedUser create(String username, Collection<String> authorities) {
        return create(username, ImmutableSet.copyOf(authorities));
    }

    public abstract String getUsername();

    public abstract ImmutableSet<String> getAuthorities();
}
