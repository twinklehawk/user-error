package net.plshark.users.auth;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

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

    public AuthProperties(String algorithm, String issuer, @DefaultValue("900000") long tokenExpiration, String secret,
                          Keystore keystore, Key key) {
        this.algorithm = algorithm;
        this.issuer = issuer;
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
    public Keystore getKeystore() {
        return keystore;
    }

    /**
     * @return parameters for the key to use for algorithms that require a key
     */
    public Key getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }

    public static class Keystore {

        private final String type;
        private final String location;
        private final String password;

        public Keystore(@DefaultValue("pkcs12") String type, String location, String password) {
            this.type = type;
            this.location = location;
            this.password = password;
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
            this.alias = alias;
            this.password = password;
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
