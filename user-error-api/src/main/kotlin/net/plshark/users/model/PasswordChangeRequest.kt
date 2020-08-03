package net.plshark.users.model

import com.fasterxml.jackson.annotation.JsonProperty

data class PasswordChangeRequest(
    @JsonProperty("current_password") val currentPassword: String,
    @JsonProperty("new_password") val newPassword: String
)
