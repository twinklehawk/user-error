package net.plshark.users;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request for changing a user's password
 */
public class PasswordChangeRequest {

    private final String currentPassword;
    private final String newPassword;

    private PasswordChangeRequest(String currentPassword, String newPassword) {
        this.currentPassword = Objects.requireNonNull(currentPassword);
        this.newPassword = Objects.requireNonNull(newPassword);
    }

    /**
     * Create a new instance
     * @param currentPassword the current password
     * @param newPassword the requested new password
     * @return the PasswordChangeRequest instance
     */
    @JsonCreator
    public static PasswordChangeRequest create(@JsonProperty("currentPassword") String currentPassword,
            @JsonProperty("newPassword") String newPassword) {
        return new PasswordChangeRequest(currentPassword, newPassword);
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
        return "PasswordChangeRequest [currentPassword=" + currentPassword + ", newPassword=" + newPassword + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currentPassword == null) ? 0 : currentPassword.hashCode());
        result = prime * result + ((newPassword == null) ? 0 : newPassword.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PasswordChangeRequest other = (PasswordChangeRequest) obj;
        if (currentPassword == null) {
            if (other.currentPassword != null)
                return false;
        } else if (!currentPassword.equals(other.currentPassword))
            return false;
        if (newPassword == null) {
            if (other.newPassword != null)
                return false;
        } else if (!newPassword.equals(other.newPassword))
            return false;
        return true;
    }
}
