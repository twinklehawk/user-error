package net.plshark.testutils

import io.r2dbc.spi.ConnectionFactories
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.springframework.r2dbc.core.DatabaseClient

class DbExtension : BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private val flyway = Flyway.configure()
        .dataSource("jdbc:postgresql://localhost:5432/postgres", "postgres", "test-pass")
        .connectRetries(10)
        .locations("classpath:/db/migration/postgres")
        .schemas("users")
        .placeholders(mutableMapOf(
            "username" to "test_user",
            "password" to "test_user_pass",
            "schema" to "users"))
        .load()

    override fun beforeEach(context: ExtensionContext) {
        flyway.migrate()
    }

    override fun afterEach(context: ExtensionContext) {
        flyway.clean()
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
        DatabaseClient::class.java == parameterContext.parameter.type

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext):
            DatabaseClient {
        val connectionFactory = ConnectionFactories.get(DB_URL)
        return DatabaseClient.create(connectionFactory)
    }

    companion object {
        const val DB_URL = "r2dbc:postgresql://test_user:test_user_pass@localhost:5432/postgres?schema=users"
    }
}
