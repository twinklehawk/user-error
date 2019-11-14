package net.plshark.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.auto.value.AutoValue;

/**
 * Request to grant a role to a user
 */
@AutoValue
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class RoleGrant {

    public static RoleGrant create(String application, String role) {
        return new AutoValue_RoleGrant(application, role);
    }

    /**
     * @return the application name of the role to grant
     */
    public abstract String getApplication();

    /**
     * @return the name of the role to grant
     */
    public abstract String getRole();
}
