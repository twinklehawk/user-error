package net.plshark.users.repo.jdbc;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.plshark.jdbc.SafePreparedStatementCreator;
import net.plshark.users.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

/**
 * User repository that uses JDBC
 */
@Repository
public class SyncJdbcUsersRepository {

    private static final String SELECT_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
    private static final String SELECT_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT = "INSERT INTO users (username, password) VALUES (?, ?) RETURNING id";
    private static final String DELETE = "DELETE FROM users WHERE id = ?";
    private static final String UPDATE_PASSWORD = "UPDATE users SET password = ? WHERE id = ? AND password = ?";

    private final JdbcOperations jdbc;
    private final UserRowMapper userRowMapper = new UserRowMapper();

    /**
     * Create a new instance
     * @param jdbc the JDBC object to use to interact with the database
     */
    public SyncJdbcUsersRepository(JdbcOperations jdbc) {
        this.jdbc = Objects.requireNonNull(jdbc, "jdbc cannot be null");
    }

    /**
     * Get a user by username
     * @param username the username
     * @return the matching user if found
     */
    public Optional<User> getForUsername(String username) {
        Objects.requireNonNull(username, "username cannot be null");
        List<User> results = jdbc.query(SELECT_BY_USERNAME, stmt -> stmt.setString(1, username), userRowMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(results));
    }

    /**
     * Create a new user
     * @param user the user
     * @return the created user
     */
    public User insert(User user) {
        if (user.getId() != null)
            throw new IllegalArgumentException("Cannot insert user with ID already set");

        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbc.update(new SafePreparedStatementCreator(
                con -> con.prepareStatement(INSERT, new String[] { "id" }),
                stmt-> {
                    stmt.setString(1, user.getUsername());
                    stmt.setString(2, user.getPassword());
                }),
            holder);
        Long id = Optional.ofNullable(holder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new JdbcUpdateAffectedIncorrectNumberOfRowsException(INSERT, 1, 0));
        return User.create(id, user.getUsername(), user.getPassword());
    }

    /**
     * Delete a user by ID
     * @param userId the user ID
     */
    public void delete(long userId) {
        jdbc.update(DELETE, stmt -> stmt.setLong(1, userId));
    }

    /**
     * Get a user by ID
     * @param id the user ID
     * @return the matching user if found
     */
    public Optional<User> getForId(long id) {
        List<User> results = jdbc.query(SELECT_BY_ID, stmt -> stmt.setLong(1, id), userRowMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(results));
    }

    /**
     * Update the password for a user
     * @param id the user ID
     * @param currentPassword the user's current password
     * @param newPassword the user's new password
     */
    public void updatePassword(long id, String currentPassword, String newPassword) {
        int updates = jdbc.update(UPDATE_PASSWORD, stmt -> {
            stmt.setString(1, newPassword);
            stmt.setLong(2, id);
            stmt.setString(3, currentPassword);
        });
        if (updates == 0)
            throw new EmptyResultDataAccessException("No matching user for password update", 1);
        else if (updates != 1)
            throw new IllegalStateException("Invalid number of rows affected: " + updates);
    }

    /**
     * Get all users up to the maximum result count
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at
     * @return the users
     */
    public List<User> getAll(int maxResults, long offset) {
        if (maxResults < 1)
            throw new IllegalArgumentException("Max results must be greater than 0");
        if (offset < 0)
            throw new IllegalArgumentException("Offset cannot be negative");

        String sql = "SELECT * FROM users ORDER BY id ASC OFFSET " + offset + " ROWS FETCH FIRST " + maxResults + " ROWS ONLY";
        return jdbc.query(sql, userRowMapper);
    }
}
