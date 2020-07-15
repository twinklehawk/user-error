package net.plshark.users.model

/** Request to grant a role to a user */
data class RoleGrant(val application: String, val role: String)
