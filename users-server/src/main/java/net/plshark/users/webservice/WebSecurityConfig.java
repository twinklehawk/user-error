package net.plshark.users.webservice;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import net.plshark.auth.throttle.LoginAttemptService;
import net.plshark.auth.throttle.LoginAttemptThrottlingFilter;
import net.plshark.auth.throttle.impl.JwtUsernameExtractor;
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

    private final Algorithm algorithm;

    public WebSecurityConfig(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Set up the security filter chain
     * @param http the spring http security configurer
     * @return the filter chain
     */
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange()
                // auth controller handles its own authentication
                .pathMatchers("/auth/**")
                    .permitAll()
                .pathMatchers("/users/**", "/roles/**")
                    .hasRole("notes-admin")
                .anyExchange()
                    .hasRole("notes-user")
            // TODO use jwt authentication
            .and().httpBasic()
            .and().addFilterAt(loginAttemptThrottlingFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
        .build();
    }

    /**
     * @return the encoder to use to encode passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private LoginAttemptThrottlingFilter loginAttemptThrottlingFilter() {
        return new LoginAttemptThrottlingFilter(loginAttemptService(), new JwtUsernameExtractor(JWT.require(algorithm).build()));
    }

    private LoginAttemptService loginAttemptService() {
        return new LoginAttemptServiceImpl();
    }
}
