package net.plshark.testutils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;
import com.opentable.db.postgres.embedded.DatabasePreparer;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

/**
 * Runs flyway migrations on an embedded test database
 */
public class PlsharkFlywayPreparer implements DatabasePreparer {

    private final FluentConfiguration flywayConfig;

    public static Builder forLocations(String... locations) {
        return new Builder(locations);
    }

    public static PlsharkFlywayPreparer defaultPreparer() {
        Map<String, String> map = new HashMap<>();
        map.put("schema", "public");
        map.put("username", "test");
        map.put("password", "pass");
        return PlsharkFlywayPreparer.forLocations("db/migration/postgres")
                .placeholders(map).build();
    }

    private PlsharkFlywayPreparer(FluentConfiguration flywayConfig) {
        this.flywayConfig = Objects.requireNonNull(flywayConfig);
    }

    @Override
    public void prepare(DataSource ds) {
        flywayConfig.dataSource(ds).load().migrate();
    }

    public static class Builder {

        private FluentConfiguration flywayConfig;

        private Builder(String... locations) {
            flywayConfig = Flyway.configure().locations(locations);
        }

        public Builder schemas(String... schemas) {
            flywayConfig.schemas(schemas);
            return this;
        }

        public Builder placeholders(Map<String, String> placeholders) {
            flywayConfig.placeholders(placeholders);
            return this;
        }

        public PlsharkFlywayPreparer build() {
            FluentConfiguration flywayConfig = this.flywayConfig;
            this.flywayConfig = null;
            return new PlsharkFlywayPreparer(flywayConfig);
        }
    }
}
