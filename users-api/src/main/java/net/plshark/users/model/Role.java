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
     * @return the ID, can be empty if not saved yet
     */
    public Optional<Long> getId() {
        return Optional.ofNullable(id);
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Role other = (Role) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
