package net.plshark.users;

import net.plshark.auth.webservice.AuthConfig;
import net.plshark.users.repo.springdata.UsersRepoSpringDataConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Application entry point
 */
@SpringBootApplication
@Import({
        UsersRepoSpringDataConfig.class,
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
