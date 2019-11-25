package net.plshark.users.auth.model;

import javax.annotation.Nonnull;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountCredentials {

    @Nonnull
    private final String username;
    @Nonnull
    private final String password;

    @JsonCreator
    public static AccountCredentials create(@JsonProperty String username, @JsonProperty String password) {
        return new AccountCredentials(username, password);
    }
}
