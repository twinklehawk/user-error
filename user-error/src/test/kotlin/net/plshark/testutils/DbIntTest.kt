package net.plshark.testutils

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

open class DbIntTest : IntTest() {

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

    @BeforeEach
    fun migrate() {
        flyway.migrate()
    }

    @AfterEach
    fun cleanup() {
        flyway.clean()
    }
}
