package net.plshark.users.auth.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticatedUser {

    private final String username;
    private final Set<String> authorities;

    @JsonCreator
    public AuthenticatedUser(@JsonProperty(value = "username", required = true) String username,
                             @JsonProperty(value = "authorities", required = true) Collection<String> authorities) {
        this.username = Objects.requireNonNull(username);
        this.authorities = Collections.unmodifiableSet(new HashSet<>(authorities));
    }

    public AuthenticatedUser(String username, String... authorities) {
        this(username, Arrays.asList(authorities));
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    @Override
    public String toString() {
        return "AuthenticatedUser{" +
                "username='" + username + '\'' +
                ", authorities=" + authorities +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticatedUser that = (AuthenticatedUser) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(authorities, that.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authorities);
    }
}
