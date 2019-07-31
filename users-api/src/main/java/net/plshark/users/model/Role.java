package net.plshark.users.model;

import com.google.auto.value.AutoValue;
import reactor.util.annotation.Nullable;

/**
 * Data for a role
 */
@AutoValue
public abstract class Role {

    public static Role create(String name, String application) {
        return create(null, name, application);
    }

    public static Role create(@Nullable Long id, String name, String application) {
        return new AutoValue_Role(id, name, application);
    }

    /**
     * @return the ID, can be null if not saved yet
     */
    @Nullable
    public abstract Long getId();

    /**
     * @return the role name
     */
    public abstract String getName();

    /**
     * @return the application this role belongs to
     */
    public abstract String getApplication();
}
