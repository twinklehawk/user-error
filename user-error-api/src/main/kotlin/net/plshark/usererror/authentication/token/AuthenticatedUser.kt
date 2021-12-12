package net.plshark.usererror.authentication.token

data class AuthenticatedUser(val username: String, val authorities: Set<String>)
