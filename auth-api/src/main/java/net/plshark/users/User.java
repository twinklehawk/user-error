package net.plshark.users;

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
        return "User [id=" + id + ", username=" + username + ", password=" + password + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
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
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }
}
