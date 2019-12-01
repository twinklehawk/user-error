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

    @Nonnull @JsonProperty("current_password")
    private final String currentPassword;
    @Nonnull @JsonProperty("new_password")
    private final String newPassword;

    @JsonCreator
    public static PasswordChangeRequest create(@JsonProperty("current_password") String currentPassword,
                                               @JsonProperty("new_password") String newPassword) {
        return new PasswordChangeRequest(currentPassword, newPassword);
    }
}
