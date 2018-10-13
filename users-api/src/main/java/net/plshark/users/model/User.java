package net.plshark.users.model;

import java.util.Objects;
import java.util.Optional;

/**
 * Data for a user
 */
public class User {

    private final Long id;
    private String username;
    private String password;

    /**
     * Create a new instance
     * @param username the username
     * @param password the password
     */
    public User(String username, String password) {
        this(null, username, password);
    }

    /**
     * Create a new instance
     * @param id the user ID, can be null
     * @param username the username
     * @param password the password, can be null
     */
    public User(Long id, String username, String password) {
        this.id = id;
        this.username = Objects.requireNonNull(username);
        this.password = password;
    }

    /**
     * @return the ID, not set if the user has not been saved yet
     */
    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = Objects.requireNonNull(username, "username cannot be null");
    }

    /**
     * @return the password, can be null when hiding the password
     */
    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    /**
     * @param password the password, can be null
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password);
    }
}
