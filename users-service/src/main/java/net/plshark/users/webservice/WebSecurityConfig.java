package net.plshark.users.webservice;

import net.plshark.auth.jwt.HttpBearerBuilder;
import net.plshark.auth.jwt.JwtReactiveAuthenticationManager;
import net.plshark.auth.service.AuthService;
import net.plshark.auth.throttle.IpThrottlingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

/**
 * Notes web security configuration
 */
@EnableWebFluxSecurity
public class WebSecurityConfig {

    private final AuthService authService;

    public WebSecurityConfig(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Set up the security filter chain
     * @param http the spring http security configurer
     * @return the filter chain
     */
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        HttpBearerBuilder builder = new HttpBearerBuilder(authenticationManager());

        return http
            .authorizeExchange()
                // auth controller handles its own authentication
                .pathMatchers("/auth/**")
                    .permitAll()
                .pathMatchers("/users/**", "/roles/**")
                    .hasRole("users-admin")
                .anyExchange()
                    .hasRole("users-user")
            .and()
                .authenticationManager(authenticationManager())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .csrf().disable()
                .logout().disable()
                .addFilterAt(builder.buildFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
                .addFilterAt(ipThrottlingFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
        .build();
    }

    @Bean
    public JwtReactiveAuthenticationManager authenticationManager() {
        return new JwtReactiveAuthenticationManager(authService);
    }

    private IpThrottlingFilter ipThrottlingFilter() {
        return new IpThrottlingFilter();
    }
}
