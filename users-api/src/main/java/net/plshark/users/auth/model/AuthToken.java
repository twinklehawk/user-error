package net.plshark.users.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import reactor.util.annotation.Nullable;

@AutoValue
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = AutoValue_AuthToken.Builder.class)
public abstract class AuthToken {

    public static final String DEFAULT_TOKEN_TYPE = "bearer";

    public static Builder builder() {
        return new AutoValue_AuthToken.Builder()
                .tokenType(DEFAULT_TOKEN_TYPE);
    }

    /**
     * @return the access token to use to authenticate to the service
     */
    @JsonProperty("access_token")
    public abstract String getAccessToken();

    /**
     * @return the token type
     */
    @JsonProperty("token_type")
    public abstract String getTokenType();

    /**
     * @return the number of seconds until this token expires
     */
    @JsonProperty("expires_in")
    public abstract long getExpiresIn();

    /**
     * @return the refresh token that can be used to generate a new token before this token expires
     */
    @Nullable
    @JsonProperty("refresh_token")
    public abstract String getRefreshToken();

    /**
     * @return the scope the user is granted, if empty then the scope is identical to the requested scope
     */
    @Nullable
    @JsonProperty("scope")
    public abstract String getScope();

    /**
     * Builder for creating an AuthToken
     */
    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract static class Builder {

        @JsonProperty("access_token")
        public abstract Builder accessToken(String accessToken);

        @JsonProperty("token_type")
        public abstract Builder tokenType(String tokenType);

        @JsonProperty("expires_in")
        public abstract Builder expiresIn(long expiresIn);

        @JsonProperty("refresh_token")
        public abstract Builder refreshToken(String refreshToken);

        @JsonProperty("scope")
        public abstract Builder scope(String scope);

        public abstract AuthToken build();
    }
}
