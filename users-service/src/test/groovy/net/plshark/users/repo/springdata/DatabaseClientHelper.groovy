package net.plshark.users.repo.springdata

import com.opentable.db.postgres.junit.PreparedDbRule
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import org.springframework.data.r2dbc.core.DatabaseClient

/**
 * Helper methods for setting up a test DB connection
 */
class DatabaseClientHelper {

    /**
     * Build a DatabaseClient using the embedded postgres from a PreparedDbRule
     * @param dbRule the test rule
     * @return the DatabaseClient
     */
    static DatabaseClient buildTestClient(PreparedDbRule dbRule) {
        return DatabaseClient.create(
                new PostgresqlConnectionFactory(
                        PostgresqlConnectionConfiguration.builder()
                                .database(dbRule.connectionInfo.dbName)
                                .host('localhost')
                                .port(dbRule.connectionInfo.port)
                                .username(dbRule.connectionInfo.user)
                                .password('')
                                .build()))
    }
}
