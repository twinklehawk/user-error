package net.plshark.usererror.authorization

data class AuthenticatedUser(val username: String, val authorities: Set<String>)
