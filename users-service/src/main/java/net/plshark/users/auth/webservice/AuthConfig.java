package net.plshark.users.auth.webservice;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import net.plshark.users.auth.AuthProperties;
import net.plshark.users.auth.service.AlgorithmBuilder;
import net.plshark.users.auth.service.AlgorithmFactory;
import net.plshark.users.auth.service.AuthService;
import net.plshark.users.auth.service.AuthServiceImpl;
import net.plshark.users.auth.service.DefaultTokenBuilder;
import net.plshark.users.auth.service.DefaultTokenVerifier;
import net.plshark.users.auth.service.TokenBuilder;
import net.plshark.users.auth.service.TokenVerifier;
import net.plshark.users.auth.service.UserAuthSettingsService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring configuration for auth services
 */
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class AuthConfig {

    @Bean
    public TokenBuilder tokenBuilder(Algorithm algorithm, AuthProperties props) {
        return new DefaultTokenBuilder(algorithm, props.getIssuer());
    }

    @Bean
    public TokenVerifier tokenVerifier(JWTVerifier jwtVerifier) {
        return new DefaultTokenVerifier(jwtVerifier);
    }

    @Bean
    public JWTVerifier jwtVerifier(Algorithm algorithm, AuthProperties props) {
        return JWT.require(algorithm).withIssuer(props.getIssuer()).build();
    }

    @Bean
    public AuthService authService(PasswordEncoder passwordEncoder, TokenVerifier tokenVerifier, TokenBuilder tokenBuilder,
                                   ReactiveUserDetailsService userDetailsService, UserAuthSettingsService userAuthSettingsService,
                                   AuthProperties props) {
        return new AuthServiceImpl(passwordEncoder, userDetailsService, tokenVerifier, tokenBuilder,
                userAuthSettingsService, props.getTokenExpiration());
    }

    @Bean
    public Algorithm algorithm(AuthProperties props, List<AlgorithmBuilder> algorithmBuilders) throws
            GeneralSecurityException, IOException {
        return new AlgorithmFactory(algorithmBuilders).buildAlgorithm(props);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
