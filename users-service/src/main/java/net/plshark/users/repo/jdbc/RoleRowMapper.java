package net.plshark.users.repo.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.plshark.users.model.Role;
import org.springframework.jdbc.core.RowMapper;

/**
 * Maps result rows to User objects
 */
class RoleRowMapper implements RowMapper<Role> {

    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Role(rs.getLong("id"), rs.getString("name"));
    }
}
