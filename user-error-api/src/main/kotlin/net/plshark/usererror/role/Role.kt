package net.plshark.usererror.role

import com.fasterxml.jackson.annotation.JsonProperty

data class Role(val id: Long, @JsonProperty("application_id") val applicationId: Long, val name: String)
