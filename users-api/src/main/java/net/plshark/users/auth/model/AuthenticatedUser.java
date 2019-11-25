package net.plshark.users.auth.model;

import javax.annotation.Nonnull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableSet;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = AuthenticatedUser.AuthenticatedUserBuilder.class)
public class AuthenticatedUser {

    @Nonnull
    private final String username;
    @Nonnull @Singular
    private final ImmutableSet<String> authorities;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthenticatedUserBuilder {

    }
}
