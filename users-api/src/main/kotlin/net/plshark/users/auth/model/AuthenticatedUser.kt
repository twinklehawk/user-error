package net.plshark.users.auth.model

data class AuthenticatedUser(val username: String, val authorities: Set<String>)
