package net.plshark.users.webservice;

import net.plshark.users.repo.jdbc.UsersRepoJdbcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import net.plshark.users.service.UsersServiceConfig;

/**
 * Application entry point
 */
@SpringBootApplication
@Import({
        UsersServiceConfig.class,
        UsersRepoJdbcConfig.class
})
@ComponentScan({
    "net.plshark.users.webservice"
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
