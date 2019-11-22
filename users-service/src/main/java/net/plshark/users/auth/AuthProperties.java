package net.plshark.users.auth;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * Properties for the auth service
 */
public class AuthProperties {

    @NotEmpty
    private String algorithm;
    @NotEmpty
    private String issuer;
    @Min(1)
    private long tokenExpiration = 15 * 60 * 1000;
    private String secret;
    @Valid
    private final Keystore keystore = new Keystore();
    @Valid
    private final Key key = new Key();

    /**
     * @return the algorithm used for signing and validating JWT
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * @param algorithm the algorithm used for signing and validating JWT
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * @return the issuer set in generated tokens and required to validate tokens
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * @param issuer the issuer set in generated tokens and required to validate tokens
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * @return the number of milliseconds after creation that a token should expire
     */
    public long getTokenExpiration() {
        return tokenExpiration;
    }

    /**
     * @param tokenExpiration the number of milliseconds after creation that a token should expire
     */
    public void setTokenExpiration(long tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
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

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public static class Keystore {

        private String type = "pkcs12";
        private String location;
        private String password;

        /**
         * @return the keystore type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type the keystore type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return the location of the keystore
         */
        public String getLocation() {
            return location;
        }

        /**
         * @param location the location of the keystore
         */
        public void setLocation(String location) {
            this.location = location;
        }

        /**
         * @return the password to access the keystore
         */
        public String getPassword() {
            return password;
        }

        /**
         * @param password the password to access the keystore
         */
        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class Key {

        private String alias;
        private String password;

        /**
         * @return the alias of the key in the keystore
         */
        public String getAlias() {
            return alias;
        }

        /**
         * @param alias the alias of the key in the keystore
         */
        public void setAlias(String alias) {
            this.alias = alias;
        }

        /**
         * @return the password to access the key
         */
        public String getPassword() {
            return password;
        }

        /**
         * @param password the password to access the key
         */
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
