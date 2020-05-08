package net.plshark.users.auth.webservice

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import net.plshark.users.auth.AuthProperties
import net.plshark.users.auth.service.AlgorithmBuilder
import net.plshark.users.auth.service.AlgorithmFactory
import net.plshark.users.auth.service.DefaultTokenBuilder
import net.plshark.users.auth.service.DefaultTokenVerifier
import net.plshark.users.auth.service.TokenBuilder
import net.plshark.users.auth.service.TokenVerifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.io.IOException
import java.security.GeneralSecurityException

/**
 * Spring configuration for auth services
 */
@Configuration
@EnableConfigurationProperties(AuthProperties::class)
class AuthConfig {
    @Bean
    fun tokenBuilder(algorithm: Algorithm, props: AuthProperties): TokenBuilder {
        return DefaultTokenBuilder(algorithm, props.issuer)
    }

    @Bean
    fun tokenVerifier(jwtVerifier: JWTVerifier): TokenVerifier {
        return DefaultTokenVerifier(jwtVerifier)
    }

    @Bean
    fun jwtVerifier(algorithm: Algorithm?, props: AuthProperties): JWTVerifier {
        return JWT.require(algorithm).withIssuer(props.issuer).build()
    }

    @Bean
    @Throws(GeneralSecurityException::class, IOException::class)
    fun algorithm(props: AuthProperties, algorithmBuilders: List<AlgorithmBuilder>): Algorithm {
        return AlgorithmFactory(algorithmBuilders).buildAlgorithm(props)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}