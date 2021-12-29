package net.plshark.usererror.authorization

data class UserAuthorities(val username: String, val authorities: Set<String>)
