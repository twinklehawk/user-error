package net.plshark.usererror.authentication.token

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthToken(
    /** the access token to use to access a service */
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("token_type") val tokenType: String = "bearer",
    /** the number of seconds until this token expires */
    @JsonProperty("expires_in") val expiresIn: Long,
    /** the refresh token that can be used to generate a new token before this token expires */
    @JsonProperty("refresh_token") val refreshToken: String?,
    /** the scope the user is granted, if empty then the scope is identical to the requested scope */
    @JsonProperty("scope") val scope: String?
)
