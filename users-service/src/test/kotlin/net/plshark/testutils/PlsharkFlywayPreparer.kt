package net.plshark.testutils

import javax.sql.DataSource
import com.opentable.db.postgres.embedded.DatabasePreparer
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration

/**
 * Runs flyway migrations on an embedded test database
 */
class PlsharkFlywayPreparer(private val flywayConfig: FluentConfiguration) : DatabasePreparer {

    companion object {
        fun forLocations(vararg locations: String): Builder {
            return Builder(*locations)
        }

        fun defaultPreparer(): PlsharkFlywayPreparer {
            val map = mapOf(
                "schema" to "",
                "username" to "test",
                "password" to "pass")
            return forLocations("db/migration/postgres").placeholders(map).build()
        }
    }

    @Override
    override fun prepare(ds: DataSource) {
        flywayConfig.dataSource(ds).load().migrate()
    }

    class Builder(vararg locations: String) {

        private val flywayConfig: FluentConfiguration = Flyway.configure().locations(*locations)
        private var built = false

        fun schemas(vararg schemas: String): Builder {
            flywayConfig.schemas(*schemas)
            return this
        }

        fun placeholders(placeholders: Map<String, String>): Builder {
            flywayConfig.placeholders(placeholders)
            return this
        }

        fun build(): PlsharkFlywayPreparer {
            if (built)
                throw IllegalStateException("Preparer already built")
            built = true
            return PlsharkFlywayPreparer(flywayConfig)
        }
    }
}
