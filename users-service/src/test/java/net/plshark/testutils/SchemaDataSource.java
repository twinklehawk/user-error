package net.plshark.testutils;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * DataSource wrapper that sets each connection to use a specific schema
 */
public class SchemaDataSource implements DataSource {

    private final DataSource dataSource;
    private final String schema;

    /**
     * Create a new instance
     * @param dataSource the DataSource to wrap
     * @param schema the schema to set on each connection
     */
    public SchemaDataSource(DataSource dataSource, String schema) {
        this.dataSource = dataSource;
        this.schema = schema;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection c = dataSource.getConnection();
        c.setSchema(schema);
        return c;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection c = dataSource.getConnection(username, password);
        c.setSchema(schema);
        return c;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (dataSource.getClass().isAssignableFrom(iface))
            return (T) dataSource;
        else
            return dataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSource.getClass().isAssignableFrom(iface) || dataSource.isWrapperFor(iface);
    }
}
