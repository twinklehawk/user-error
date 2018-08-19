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

    public static GenerateTokenResponseBuilder builder() {
        return new GenerateTokenResponseBuilder().withTokenType(DEFAULT_TOKEN_TYPE);
    }

    public static class GenerateTokenResponseBuilder {
        private String accessToken;
        private String tokenType;
        private long expiresIn;
        private String refreshToken;
        private String scope;

        private GenerateTokenResponseBuilder() {

        }

        public GenerateTokenResponseBuilder withAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public GenerateTokenResponseBuilder withTokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public GenerateTokenResponseBuilder withExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public GenerateTokenResponseBuilder withRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public GenerateTokenResponseBuilder withScope(String scope) {
            this.scope = scope;
            return this;
        }

        public AuthToken build() {
            return new AuthToken(accessToken, tokenType, expiresIn, refreshToken, scope);
        }
    }
}
