package net.plshark.testutils

import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

/**
 * DataSource wrapper that sets each connection to use a specific schema
 */
class SchemaDataSource(private val dataSource: DataSource, private val schema: String) : DataSource by dataSource {

    @Throws(SQLException::class)
    override fun getConnection() : Connection {
        val c = dataSource.connection
        c.schema = schema
        return c
    }

    @Throws(SQLException::class)
    override fun getConnection(username: String, password: String): Connection {
        val c = dataSource.getConnection(username, password)
        c.schema = schema
        return c
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(SQLException::class)
    override fun <T> unwrap(iface: Class<T>): T {
        return if (dataSource.javaClass.isAssignableFrom(iface)) dataSource as T
            else dataSource.unwrap(iface)
    }

    @Throws(SQLException::class)
    override fun isWrapperFor(iface: Class<*>): Boolean {
        return dataSource.javaClass.isAssignableFrom(iface) || dataSource.isWrapperFor(iface)
    }
}
