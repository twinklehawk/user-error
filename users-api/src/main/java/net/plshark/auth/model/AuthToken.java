package net.plshark.auth.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthToken {

    public static final String DEFAULT_TOKEN_TYPE = "bearer";

    @JsonProperty("access_token")
    private final String accessToken;
    @JsonProperty("token_type")
    private final String tokenType;
    @JsonProperty("expires_in")
    private final long expiresIn;
    @JsonProperty("refresh_token")
    private final String refreshToken;
    @JsonProperty("scope")
    private final String scope;

    @JsonCreator
    public AuthToken(@JsonProperty(value = "access_token", required = true) String accessToken,
                     @JsonProperty(value = "token_type", required = true) String tokenType,
                     @JsonProperty(value = "expires_in", required = true) long expiresIn,
                     @JsonProperty("refresh_token") String refreshToken,
                     @JsonProperty("scope") String scope) {
        this.accessToken = Objects.requireNonNull(accessToken, "access token cannot be null");
        this.tokenType = Objects.requireNonNull(tokenType, "token type cannot be null");
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }

    /**
     * @return the access token to use to authenticate to the service
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @return the token type
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * @return the number of seconds until this token expires
     */
    public long getExpiresIn() {
        return expiresIn;
    }

    /**
     * @return the refresh token that can be used to generate a new token before this token expires
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * @return the scope the user is granted, if empty then the scope is identical to the requested scope
     */
    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "AuthToken{" +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", scope='" + scope + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken = (AuthToken) o;
        return expiresIn == authToken.expiresIn &&
                Objects.equals(accessToken, authToken.accessToken) &&
                Objects.equals(tokenType, authToken.tokenType) &&
                Objects.equals(refreshToken, authToken.refreshToken) &&
                Objects.equals(scope, authToken.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, tokenType, expiresIn, refreshToken, scope);
    }

    /**
     * Builder for creating an AuthToken
     */
    public static class Builder {
        private String accessToken;
        private String tokenType = DEFAULT_TOKEN_TYPE;
        private long expiresIn;
        private String refreshToken;
        private String scope;

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder expiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public AuthToken build() {
            return new AuthToken(accessToken, tokenType, expiresIn, refreshToken, scope);
        }
    }
}
