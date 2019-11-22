package net.plshark.users.auth.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AccountCredentials {

    @JsonCreator
    public static AccountCredentials create(@JsonProperty String username, @JsonProperty String password) {
        return new AutoValue_AccountCredentials(username, password);
    }

    public abstract String getUsername();

    public abstract String getPassword();
}
