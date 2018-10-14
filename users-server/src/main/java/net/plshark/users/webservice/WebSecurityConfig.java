package net.plshark.users.webservice;

import net.plshark.auth.throttle.LoginAttemptService;
import net.plshark.auth.throttle.LoginAttemptThrottlingFilter;
import net.plshark.auth.throttle.impl.BasicAuthenticationUsernameExtractor;
import net.plshark.auth.throttle.impl.LoginAttemptServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Notes web security configuration
 */
@EnableWebFluxSecurity
public class WebSecurityConfig {

    /**
     * Set up the security filter chain
     * @param http the spring http security configurer
     * @return the filter chain
     */
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange()
                .pathMatchers("/users/**", "/roles/**")
                    .hasRole("notes-admin")
                .anyExchange()
                    .hasRole("notes-user")
            // use basic authentication
            .and().httpBasic()
            .and().addFilterAt(loginAttemptThrottlingFilter(), SecurityWebFiltersOrder.HTTP_BASIC);
        return http.build();
    }

    /**
     * @return the encoder to use to encode passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private LoginAttemptThrottlingFilter loginAttemptThrottlingFilter() {
        return new LoginAttemptThrottlingFilter(loginAttemptService(), new BasicAuthenticationUsernameExtractor());
    }

    private LoginAttemptService loginAttemptService() {
        return new LoginAttemptServiceImpl();
    }
}
