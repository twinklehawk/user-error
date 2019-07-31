package net.plshark.users.model;

import java.util.Objects;
import com.google.auto.value.AutoValue;

/**
 * Information about a user safe to return to clients
 */
@AutoValue
public abstract class UserInfo {

    public static UserInfo create(long id, String username) {
        return new AutoValue_UserInfo(id, username);
    }

    /**
     * Create a UserInfo from a User
     * @param user the User
     * @return the UserInfo
     */
    public static UserInfo fromUser(User user) {
        Objects.requireNonNull(user.getId());
        return UserInfo.create(user.getId(), user.getUsername());
    }

    /**
     * @return the ID
     */
    public abstract long getId();

    /**
     * @return the username
     */
    public abstract String getUsername();
}
