package net.plshark.usererror.user

import com.fasterxml.jackson.annotation.JsonProperty

data class UserAuthSettings(
    val id: Long?,
    @JsonProperty("user_id") val userId: Long?,
    @JsonProperty("refresh_token_enabled") val refreshTokenEnabled: Boolean = true,
    /** auth token expiration time in milliseconds */
    @JsonProperty("auth_token_expiration") val authTokenExpiration: Long?,
    /** refresh token expiration time in milliseconds */
    @JsonProperty("refresh_token_expiration") val refreshTokenExpiration: Long?
)
