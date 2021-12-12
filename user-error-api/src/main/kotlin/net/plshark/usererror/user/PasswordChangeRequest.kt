package net.plshark.usererror.user

import com.fasterxml.jackson.annotation.JsonProperty

data class PasswordChangeRequest(
    @JsonProperty("current_password") val currentPassword: String,
    @JsonProperty("new_password") val newPassword: String
)
