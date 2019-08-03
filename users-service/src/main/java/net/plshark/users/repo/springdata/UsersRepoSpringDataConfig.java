package net.plshark.users.repo.springdata;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import reactor.util.annotation.NonNull;

/**
 * Spring configuration for the users spring data repository
 */
@Configuration
@ComponentScan
public class UsersRepoSpringDataConfig extends AbstractR2dbcConfiguration {

    @Value("${spring.r2dbc.url}")
    private String r2dbcUrl;

    @Override
    @NonNull
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(r2dbcUrl);
    }
}
