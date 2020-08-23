package net.plshark.users.model

import com.fasterxml.jackson.annotation.JsonProperty

/** Request to grant a role to a user */
data class RoleGrant(@JsonProperty("application_id") val applicationId: Long, val role: String)
