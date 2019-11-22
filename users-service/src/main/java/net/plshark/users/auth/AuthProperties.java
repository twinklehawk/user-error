package net.plshark.users.auth;

import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import reactor.util.annotation.Nullable;

/**
 * Properties for the auth service
 */
@ConfigurationProperties("auth")
@ConstructorBinding
public class AuthProperties {

    @NotEmpty
    private final String algorithm;
    @NotEmpty
    private final String issuer;
    @Min(1)
    private final long tokenExpiration;
    private final String secret;
    @Valid
    private final Keystore keystore;
    @Valid
    private final Key key;

    public static AuthProperties forNone(String algorithm, String issuer, long tokenExpiration) {
        return new AuthProperties(algorithm, issuer, tokenExpiration, null, null, null);
    }

    public static AuthProperties forSecret(String algorithm, String issuer, long tokenExpiration, String secret) {
        return new AuthProperties(algorithm, issuer, tokenExpiration, secret, null, null);
    }

    public static AuthProperties forKeystore(String algorithm, String issuer, long tokenExpiration, Keystore keystore,
                                             Key key) {
        return new AuthProperties(algorithm, issuer, tokenExpiration, null, keystore, key);
    }

    public AuthProperties(String algorithm, String issuer, @DefaultValue("900000") long tokenExpiration,
                          @Nullable String secret, @Nullable Keystore keystore, @Nullable Key key) {
        this.algorithm = Objects.requireNonNull(algorithm, "algorithm cannot be null");
        this.issuer = Objects.requireNonNull(issuer, "issuer cannot be null");
        this.tokenExpiration = tokenExpiration;
        this.secret = secret;
        this.keystore = keystore;
        this.key = key;
    }

    /**
     * @return the algorithm used for signing and validating JWT
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * @return the issuer set in generated tokens and required to validate tokens
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * @return the number of milliseconds after creation that a token should expire
     */
    public long getTokenExpiration() {
        return tokenExpiration;
    }

    /**
     * @return parameters for the keystore to use for algorithms that require a key
     */
    @Nullable
    public Keystore getKeystore() {
        return keystore;
    }

    /**
     * @return parameters for the key to use for algorithms that require a key
     */
    @Nullable
    public Key getKey() {
        return key;
    }

    /**
     * @return the secret for use in algorithms that require a secret
     */
    @Nullable
    public String getSecret() {
        return secret;
    }

    public static class Keystore {

        private final String type;
        private final String location;
        private final String password;

        public Keystore(@DefaultValue("pkcs12") String type, String location, String password) {
            this.type = Objects.requireNonNull(type, "type cannot be null");
            this.location = Objects.requireNonNull(location, "location cannot be null");
            this.password = Objects.requireNonNull(password, "password cannot be null");
        }

        /**
         * @return the keystore type
         */
        public String getType() {
            return type;
        }

        /**
         * @return the location of the keystore
         */
        public String getLocation() {
            return location;
        }

        /**
         * @return the password to access the keystore
         */
        public String getPassword() {
            return password;
        }
    }

    public static class Key {

        private final String alias;
        private final String password;

        public Key(String alias, String password) {
            this.alias = Objects.requireNonNull(alias, "alias cannot be null");
            this.password = Objects.requireNonNull(password, "password cannot be null");
        }

        /**
         * @return the alias of the key in the keystore
         */
        public String getAlias() {
            return alias;
        }

        /**
         * @return the password to access the key
         */
        public String getPassword() {
            return password;
        }
    }
}
