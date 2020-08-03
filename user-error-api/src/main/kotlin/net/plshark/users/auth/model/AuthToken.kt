package net.plshark.users.auth.model

import com.fasterxml.jackson.annotation.JsonProperty

const val DEFAULT_TOKEN_TYPE = "bearer"

data class AuthToken(
    /** the access token to use to authenticate to the service */
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("token_type") val tokenType: String = DEFAULT_TOKEN_TYPE,
    /** the number of seconds until this token expires */
    @JsonProperty("expires_in") val expiresIn: Long,
    /** the refresh token that can be used to generate a new token before this token expires */
    @JsonProperty("refresh_token") val refreshToken: String?,
    /** the scope the user is granted, if empty then the scope is identical to the requested scope */
    @JsonProperty("scope") val scope: String?
)
