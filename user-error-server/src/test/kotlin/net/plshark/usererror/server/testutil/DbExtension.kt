package net.plshark.usererror.server.testutil

import io.r2dbc.spi.ConnectionFactories
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.springframework.r2dbc.core.DatabaseClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class DbExtension : BeforeEachCallback, AfterEachCallback, ParameterResolver {

    companion object {
        private val NAMESPACE = ExtensionContext.Namespace.create(DbExtension::class.java)
        private const val POSTGRES_CONTAINER = "postgresContainer"
    }

    override fun beforeEach(context: ExtensionContext) {
        val store = context.getStore(NAMESPACE)
        val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:13.5"))
            .withDatabaseName("user-error")
            .withUsername("test_user")
            .withPassword("test_password")
        store.put(POSTGRES_CONTAINER, postgres)
        postgres.start()
        val flyway = buildFlyway(postgres.jdbcUrl, postgres.username, postgres.password)
        flyway.migrate()
    }

    override fun afterEach(context: ExtensionContext) {
        val postgres = getPostgresContainer(context)
        postgres.stop()
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
        DatabaseClient::class.java == parameterContext.parameter.type

    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): DatabaseClient {
        val postgres = getPostgresContainer(extensionContext)
        val url =
            "r2dbc:postgresql://${postgres.username}:${postgres.password}@${postgres.containerIpAddress}:" +
                "${postgres.firstMappedPort}/${postgres.databaseName}?schema=users"
        val connectionFactory = ConnectionFactories.get(url)
        return DatabaseClient.create(connectionFactory)
    }

    private fun getPostgresContainer(context: ExtensionContext): PostgreSQLContainer<*> {
        val store = context.getStore(NAMESPACE)
        return store[POSTGRES_CONTAINER] as PostgreSQLContainer<*>
    }

    private fun buildFlyway(jdbcUrl: String, username: String, password: String): Flyway {
        return Flyway.configure()
            .dataSource(jdbcUrl, username, password)
            .locations("classpath:/db/migration/postgres")
            .schemas("users")
            .placeholders(
                mutableMapOf(
                    "username" to "test_user",
                    "password" to "test_user_pass",
                    "schema" to "users"
                )
            ).load()
    }
}
