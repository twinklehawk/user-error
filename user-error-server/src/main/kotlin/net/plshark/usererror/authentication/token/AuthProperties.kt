package net.plshark.usererror.authentication.token

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty

/**
 * Properties for the auth service
 */
@ConfigurationProperties("auth")
@ConstructorBinding
class AuthProperties(
    /** the algorithm used for signing and validating JWT */
    @NotEmpty val algorithm: String,
    /** the issuer set in generated tokens and required to validate tokens */
    @NotEmpty val issuer: String,
    /** the number of milliseconds after creation that a token should expire */
    @Min(1) val tokenExpiration: Long = 900000,
    /** the secret for use in algorithms that require a secret */
    val secret: String?,
    /** parameters for the keystore to use for algorithms that require a key */
    @Valid val keystore: Keystore?,
    /** parameters for the key to use for algorithms that require a key */
    @Valid val key: Key?
) {
    class Keystore(
        /** the keystore type */
        @NotEmpty val type: String = "pkcs12",
        /** the location of the keystore */
        @NotEmpty val location: String,
        /** the password to access the keystore */
        @NotEmpty val password: String
    )

    class Key(
        /** the alias of the key in the keystore */
        @NotEmpty val alias: String,
        /** the password to access the key */
        @NotEmpty val password: String
    )

    companion object {
        fun forNone(issuer: String, tokenExpiration: Long): AuthProperties {
            return AuthProperties(
                NoneAlgorithmBuilder.NONE,
                issuer,
                tokenExpiration,
                null,
                null,
                null
            )
        }

        fun forSecret(algorithm: String, issuer: String, tokenExpiration: Long, secret: String): AuthProperties {
            return AuthProperties(algorithm, issuer, tokenExpiration, secret, null, null)
        }

        fun forKeystore(algorithm: String, issuer: String, tokenExpiration: Long, keystore: Keystore, key: Key):
            AuthProperties {
            return AuthProperties(algorithm, issuer, tokenExpiration, null, keystore, key)
        }
    }
}
