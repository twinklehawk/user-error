package net.plshark.users.repo.jdbc;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Named;
import javax.inject.Singleton;

import net.plshark.users.model.Role;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import net.plshark.jdbc.SafePreparedStatementCreator;

/**
 * Role repository that uses JDBC
 */
@Named
@Singleton
public class SyncJdbcRolesRepository {

    private static final String INSERT = "INSERT INTO roles (name, application) VALUES (?, ?) RETURNING id";
    private static final String DELETE = "DELETE FROM roles WHERE id = ?";
    private static final String SELECT_BY_ID = "SELECT * FROM roles WHERE id = ?";
    private static final String SELECT_BY_NAME = "SELECT * FROM roles WHERE name = ?";

    private final JdbcOperations jdbc;
    private final RoleRowMapper roleRowMapper = new RoleRowMapper();

    /**
     * Create a new instance
     * @param jdbc the JDBC object to use to interact with the database
     */
    public SyncJdbcRolesRepository(JdbcOperations jdbc) {
        this.jdbc = Objects.requireNonNull(jdbc, "jdbc cannot be null");
    }

    /**
     * Insert a role
     * @param role the role
     * @return the inserted role
     * @throws DataAccessException if the insert fails
     */
    public Role insert(Role role) {
        if (role.getId() != null)
            throw new IllegalArgumentException("Cannot insert role with ID already set");

        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbc.update(new SafePreparedStatementCreator(
                con -> con.prepareStatement(INSERT, new String[] { "id" }),
                stmt -> {
                    stmt.setString(1, role.getName());
                    stmt.setString(2, role.getApplication());
                }),
            holder);
        Long id = Optional.ofNullable(holder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new JdbcUpdateAffectedIncorrectNumberOfRowsException(INSERT, 1, 0));
        return Role.create(id, role.getName(), role.getApplication());
    }

    /**
     * Delete a role by ID
     * @param roleId the role ID
     * @throws DataAccessException if the delete fails
     */
    public void delete(long roleId) {
        jdbc.update(DELETE, stmt -> stmt.setLong(1, roleId));
    }

    /**
     * Get a role by ID
     * @param id the role ID
     * @return the matching role if found
     * @throws DataAccessException if the query fails
     */
    public Optional<Role> getForId(long id) {
        List<Role> results = jdbc.query(SELECT_BY_ID, stmt -> stmt.setLong(1, id), roleRowMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(results));
    }

    /**
     * Get a role by name
     * @param name the role name
     * @return the matching role if found
     * @throws DataAccessException if the query fails
     */
    public Optional<Role> getForName(String name) {
        Objects.requireNonNull(name, "name cannot be null");
        List<Role> results = jdbc.query(SELECT_BY_NAME, stmt -> stmt.setString(1, name), roleRowMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(results));
    }

    /**
     * Get all roles up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return the roles
     */
    public List<Role> getRoles(int maxResults, long offset) {
        if (maxResults < 1)
            throw new IllegalArgumentException("Max results must be greater than 0");
        if (offset < 0)
            throw new IllegalArgumentException("Offset cannot be negative");

        String sql = "SELECT * FROM roles ORDER BY id OFFSET " + offset + " ROWS FETCH FIRST " + maxResults + " ROWS ONLY";
        return jdbc.query(sql, roleRowMapper);
    }
}
