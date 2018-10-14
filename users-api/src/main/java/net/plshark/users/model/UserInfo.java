package net.plshark.users.model;

import java.util.Objects;

/**
 * Information about a user safe to return to clients
 */
public class UserInfo {

    private final long id;
    private final String username;

    public UserInfo(long id, String username) {
        this.id = id;
        this.username = Objects.requireNonNull(username, "username cannot be null");
    }

    /**
     * Create a UserInfo from a User
     * @param user the User
     * @return the UserInfo
     */
    public static UserInfo fromUser(User user) {
        return new UserInfo(user.getId(), user.getUsername());
    }

    /**
     * @return the ID
     */
    public long getId() {
        return id;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return id == userInfo.id &&
                Objects.equals(username, userInfo.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
