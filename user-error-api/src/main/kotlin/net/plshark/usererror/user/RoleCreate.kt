package net.plshark.usererror.user

import com.fasterxml.jackson.annotation.JsonProperty

data class RoleCreate(@JsonProperty("application_id") val applicationId: Long, val name: String)
