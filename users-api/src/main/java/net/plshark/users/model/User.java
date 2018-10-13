package net.plshark.users.model;

import java.util.Objects;

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
     * @param id the user ID, can be null if not saved yet
     * @param username the username
     * @param password the password
     */
    public User(Long id, String username, String password) {
        this.id = id;
        setUsername(username);
        setPassword(password);
    }

    /**
     * @return the ID, null if the user has not been saved yet
     */
    public Long getId() {
        return id;
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
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password, can be null
     */
    public void setPassword(String password) {
        this.password = Objects.requireNonNull(password, "password cannot be null");
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
