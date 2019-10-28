package net.plshark.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

/**
 * Request for changing a user's password
 */
@AutoValue
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class PasswordChangeRequest {

    /**
     * Create a new instance
     * @param currentPassword the current password
     * @param newPassword the requested new password
     */
    @JsonCreator
    public static PasswordChangeRequest create(@JsonProperty("currentPassword") String currentPassword,
                                               @JsonProperty("newPassword") String newPassword) {
        return new AutoValue_PasswordChangeRequest(currentPassword, newPassword);
    }

    /**
     * @return the current password
     */
    public abstract String getCurrentPassword();

    /**
     * @return the requested new password
     */
    public abstract String getNewPassword();
}
