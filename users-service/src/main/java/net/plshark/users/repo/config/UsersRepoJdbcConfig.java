package net.plshark.users.repo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for the users JDBC repository
 */
@Configuration
@ComponentScan("net.plshark.users.repo.jdbc")
public class UsersRepoJdbcConfig {

}
