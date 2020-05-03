package net.plshark.users.webservice

import net.plshark.users.auth.jwt.HttpBearerBuilder
import net.plshark.users.auth.jwt.JwtReactiveAuthenticationManager
import net.plshark.users.auth.service.AuthService
import net.plshark.users.auth.throttle.IpThrottlingFilter
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

/**
 * Notes web security configuration
 */
@EnableWebFluxSecurity
class WebSecurityConfig(private val authService: AuthService) {

    /**
     * Set up the security filter chain
     * @param http the spring http security configurer
     * @return the filter chain
     */
    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val builder = HttpBearerBuilder(authenticationManager())
        return http
            .authorizeExchange()
            // auth controller handles its own authentication
            .pathMatchers("/auth/**").permitAll()
            .pathMatchers("/users/**", "/groups/**", "/applications/**").hasRole("users-admin")
            .anyExchange().hasRole("users-user")
            .and()
            .authenticationManager(authenticationManager())
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .csrf().disable()
            .logout().disable()
            .addFilterAt(builder.buildFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
            .addFilterAt(ipThrottlingFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
            .build()
    }

    @Bean
    fun authenticationManager(): JwtReactiveAuthenticationManager {
        return JwtReactiveAuthenticationManager(authService)
    }

    private fun ipThrottlingFilter(): IpThrottlingFilter {
        return IpThrottlingFilter()
    }
}