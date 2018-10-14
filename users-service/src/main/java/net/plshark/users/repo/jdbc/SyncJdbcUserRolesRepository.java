package net.plshark.users.repo.jdbc;

import java.util.List;
import java.util.Objects;

import javax.inject.Named;
import javax.inject.Singleton;

import net.plshark.users.model.Role;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

/**
 * User roles repository that uses JDBC
 */
@Named
@Singleton
public class SyncJdbcUserRolesRepository {

    private static final String SELECT_ROLES_FOR_USER = "SELECT r.id id, r.name name FROM roles r INNER JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?";
    private static final String INSERT_USER_ROLE = "INSERT INTO user_roles (user_id, role_id) values (?, ?)";
    private static final String DELETE_USER_ROLE = "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?";
    private static final String DELETE_USER_ROLES_BY_USER = "DELETE FROM user_roles WHERE user_id = ?";
    private static final String DELETE_USER_ROLES_BY_ROLE = "DELETE FROM user_roles WHERE role_id = ?";

    private final JdbcOperations jdbc;
    private final RoleRowMapper roleRowMapper = new RoleRowMapper();

    /**
     * Create a new instance
     * @param jdbc the JDBC object to use to interact with the database
     */
    public SyncJdbcUserRolesRepository(JdbcOperations jdbc) {
        this.jdbc = Objects.requireNonNull(jdbc, "jdbc cannot be null");
    }

    /**
     * Get all roles for a user
     * @param userId the user ID
     * @return the roles
     * @throws DataAccessException if the query fails
     */
    public List<Role> getRolesForUser(long userId) {
        return jdbc.query(SELECT_ROLES_FOR_USER, stmt -> stmt.setLong(1, userId), roleRowMapper);
    }

    /**
     * Add a role to a user
     * @param userId the user ID
     * @param roleId the role ID
     */
    public void insertUserRole(long userId, long roleId) {
        jdbc.update(INSERT_USER_ROLE, stmt -> {
            stmt.setLong(1, userId);
            stmt.setLong(2, roleId);
        });
    }

    /**
     * Remove a role from a user
     * @param userId the user ID
     * @param roleId the role ID
     */
    public void deleteUserRole(long userId, long roleId) {
        jdbc.update(DELETE_USER_ROLE, stmt -> {
            stmt.setLong(1, userId);
            stmt.setLong(2, roleId);
        });
    }

    /**
     * Remove all roles from a user
     * @param userId the user ID
     */
    public void deleteUserRolesForUser(long userId) {
        jdbc.update(DELETE_USER_ROLES_BY_USER, stmt -> stmt.setLong(1, userId));
    }

    /**
     * Remove a role from all users
     * @param roleId the role ID
     */
    public void deleteUserRolesForRole(long roleId) {
        jdbc.update(DELETE_USER_ROLES_BY_ROLE, stmt -> stmt.setLong(1, roleId));
    }
}
