package net.plshark.users.repo.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.plshark.users.model.User;
import org.springframework.jdbc.core.RowMapper;

/**
 * Maps result rows to User objects
 */
class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.create(rs.getLong("id"), rs.getString("username"), rs.getString("password"));
    }
}
