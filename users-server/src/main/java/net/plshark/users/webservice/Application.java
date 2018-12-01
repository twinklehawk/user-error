package net.plshark.users.webservice;

import net.plshark.auth.webservice.AuthConfig;
import net.plshark.users.repo.jdbc.UsersRepoJdbcConfig;
import net.plshark.users.service.UsersServiceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Application entry point
 */
@SpringBootApplication
@Import({
        UsersServiceConfig.class,
        UsersRepoJdbcConfig.class,
        AuthConfig.class
})
public class Application {

    /**
     * Application entry point
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
