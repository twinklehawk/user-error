package net.plshark.users.model;

import javax.annotation.Nonnull;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Request for changing a user's password
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PasswordChangeRequest {

    @Nonnull
    private final String currentPassword;
    @Nonnull
    private final String newPassword;

    @JsonCreator
    public static PasswordChangeRequest create(@JsonProperty String currentPassword, @JsonProperty String newPassword) {
        return new PasswordChangeRequest(currentPassword, newPassword);
    }
}
