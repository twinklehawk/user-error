package net.plshark.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;

/**
 * A safe PreparedStatementCreator that handles closing the PreparedStatement if an exception
 * happens after creation
 */
public class SafePreparedStatementCreator implements PreparedStatementCreator {

    private final PreparedStatementCreator creator;
    private final PreparedStatementSetter setter;

    /**
     * Create a new instance
     * @param creator the PreparedStatementCreator to use to create the PreparedStatement. It should not
     *            do anything besides create the PreparedStatement
     * @param setter the the PreparedStatementSetter to perform any additional actions on the
     *            PreparedStatement
     */
    public SafePreparedStatementCreator(PreparedStatementCreator creator, PreparedStatementSetter setter) {
        this.creator = creator;
        this.setter = setter;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = creator.createPreparedStatement(con);
        try {
            setter.setValues(ps);
        } catch (Throwable t) {
            try {
                ps.close();
            } catch (Exception suppressed) {
                if (t != suppressed)
                    t.addSuppressed(suppressed);
            }
            throw t;
        }
        return ps;
    }
}
