package net.plshark.usererror.authentication.token.spring

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

/**
 * Authentication implementation for use with JWT authentication
 */
data class JwtAuthenticationToken(
    val username: String?,
    val token: String?,
    val authenticated: Boolean,
    val authorities: Set<GrantedAuthority>
) : Authentication {

    override fun getCredentials() = token

    override fun getDetails() = null

    override fun getPrincipal() = username

    override fun getName() = username

    override fun isAuthenticated() = authenticated

    override fun setAuthenticated(isAuthenticated: Boolean) {
        throw IllegalArgumentException("Cannot change authenticated state after creation")
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities.toMutableSet()
    }
}
