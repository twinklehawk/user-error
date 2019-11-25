package net.plshark.users.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = AuthToken.AuthTokenBuilder.class)
public class AuthToken {

    public static final String DEFAULT_TOKEN_TYPE = "bearer";

    /** the access token to use to authenticate to the service */
    @NonNull @JsonProperty("access_token")
    private final String accessToken;

    /** the token type */
    @NonNull @Builder.Default @JsonProperty("token_type")
    private final String tokenType = DEFAULT_TOKEN_TYPE;

    /** the number of seconds until this token expires */
    @JsonProperty("expires_in")
    private final long expiresIn;

    /** the refresh token that can be used to generate a new token before this token expires */
    @Nullable @JsonProperty("refresh_token")
    private final String refreshToken;

    /** the scope the user is granted, if empty then the scope is identical to the requested scope */
    @Nullable @JsonProperty("scope")
    private final String scope;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthTokenBuilder {
/*
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
 */
    }
}
