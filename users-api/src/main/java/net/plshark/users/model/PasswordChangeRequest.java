package net.plshark.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import reactor.util.annotation.NonNull;

/**
 * Request for changing a user's password
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PasswordChangeRequest {

    @NonNull
    private final String currentPassword;
    @NonNull
    private final String newPassword;

    @JsonCreator
    public static PasswordChangeRequest create(@JsonProperty String currentPassword, @JsonProperty String newPassword) {
        return new PasswordChangeRequest(currentPassword, newPassword);
    }
}
