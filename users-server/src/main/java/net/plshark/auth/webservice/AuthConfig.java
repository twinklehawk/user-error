package net.plshark.auth.webservice;

import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.validation.Valid;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import net.plshark.auth.AuthProperties;
import net.plshark.auth.service.AlgorithmFactory;
import net.plshark.auth.service.AuthService;
import net.plshark.auth.service.AuthServiceImpl;
import net.plshark.auth.service.DefaultTokenBuilder;
import net.plshark.auth.service.DefaultTokenVerifier;
import net.plshark.auth.service.TokenBuilder;
import net.plshark.auth.service.TokenVerifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring configuration for auth services
 */
@Configuration
@ComponentScan("net.plshark.auth.webservice")
public class AuthConfig {

    @Bean
    public TokenBuilder tokenBuilder(Algorithm algorithm, AuthProperties props) {
        return new DefaultTokenBuilder(algorithm, props.getIssuer());
    }

    @Bean
    public TokenVerifier tokenVerifier(Algorithm algorithm, AuthProperties props) {
        return new DefaultTokenVerifier(JWT.require(algorithm).withIssuer(props.getIssuer()).build());
    }

    @Bean
    public AuthService authService(PasswordEncoder passwordEncoder, TokenVerifier tokenVerifier, TokenBuilder tokenBuilder,
                                   ReactiveUserDetailsService userDetailsService, AuthProperties props) {
        return new AuthServiceImpl(passwordEncoder, userDetailsService, tokenVerifier, tokenBuilder, props.getTokenExpiration());
    }

    @Bean
    public Algorithm algorithm(AuthProperties props) throws GeneralSecurityException, IOException {
        return new AlgorithmFactory().buildAlgorithm(props);
    }

    @Bean
    @Valid
    @ConfigurationProperties("auth")
    public AuthProperties authProperties() {
        return new AuthProperties();
    }
}
