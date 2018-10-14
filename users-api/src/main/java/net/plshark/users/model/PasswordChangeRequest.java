package net.plshark.users.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request for changing a user's password
 */
public class PasswordChangeRequest {

    private final String currentPassword;
    private final String newPassword;

    /**
     * Create a new instance
     * @param currentPassword the current password
     * @param newPassword the requested new password
     */
    @JsonCreator
    public PasswordChangeRequest(@JsonProperty("currentPassword") String currentPassword,
                                 @JsonProperty("newPassword") String newPassword) {
        this.currentPassword = Objects.requireNonNull(currentPassword);
        this.newPassword = Objects.requireNonNull(newPassword);
    }

    /**
     * @return the current password
     */
    public String getCurrentPassword() {
        return currentPassword;
    }

    /**
     * @return the requested new password
     */
    public String getNewPassword() {
        return newPassword;
    }

    @Override
    public String toString() {
        return "PasswordChangeRequest{}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordChangeRequest that = (PasswordChangeRequest) o;
        return Objects.equals(currentPassword, that.currentPassword) &&
                Objects.equals(newPassword, that.newPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPassword, newPassword);
    }
}
