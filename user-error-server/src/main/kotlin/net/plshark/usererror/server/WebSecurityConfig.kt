package net.plshark.usererror.server

import net.plshark.usererror.authentication.token.spring.HttpBearerBuilder
import net.plshark.usererror.authorization.token.TokenAuthorizationService
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

/**
 * Notes web security configuration
 */
@EnableWebFluxSecurity
class WebSecurityConfig(authService: TokenAuthorizationService) {

    private val builder: HttpBearerBuilder = HttpBearerBuilder(authService)

    /**
     * Set up the security filter chain
     * @param http the spring http security configurer
     * @return the filter chain
     */
    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .authorizeExchange()
            .pathMatchers("/authentication/**").permitAll()
            .pathMatchers("/authorization/authorities").permitAll()
            .pathMatchers(HttpMethod.GET, "/applications/**").hasRole("view-applications")
            .pathMatchers("/applications/**").hasRole("edit-applications")
            .pathMatchers(HttpMethod.GET, "/roles/**").hasRole("view-roles")
            .pathMatchers("/roles/**").hasRole("edit-roles")
            .pathMatchers(HttpMethod.GET, "/groups/**").hasRole("view-groups")
            .pathMatchers("/groups/**").hasRole("edit-groups")
            .pathMatchers(HttpMethod.GET, "/users/**").hasRole("view-users")
            .pathMatchers("/users/**").hasRole("edit-users")
            // reject anything else in case something was missed above
            .anyExchange().denyAll()
            .and()
            .authenticationManager(authenticationManager())
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .csrf().disable()
            .logout().disable()
            .addFilterAt(builder.buildFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
            .build()
    }

    @Bean
    fun authenticationManager(): ReactiveAuthenticationManager {
        return builder.authenticationManager
    }
}
