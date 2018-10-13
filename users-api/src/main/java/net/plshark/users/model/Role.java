package net.plshark.users.model;

import java.util.Objects;
import java.util.Optional;

/**
 * Data for a role
 */
public class Role {

    private final Long id;
    private String name;

    /**
     * Create a new instance
     * @param name the role name
     */
    public Role(String name) {
        this(null, name);
    }

    /**
     * Create a new instance
     * @param id the role ID, can be null
     * @param name the role name
     */
    public Role(Long id, String name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name cannot be null");
    }

    /**
     * @return the ID, can be null if not saved yet
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the role name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the role name
     */
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
    }

    @Override
    public String toString() {
        return "Role [id=" + id + ", name=" + name + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) &&
                Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
