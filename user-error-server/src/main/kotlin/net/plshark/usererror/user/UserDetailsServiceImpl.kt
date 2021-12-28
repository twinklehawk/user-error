package net.plshark.usererror.user

import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import net.plshark.usererror.role.Role
import net.plshark.usererror.role.UserGroupsRepository
import net.plshark.usererror.role.UserRolesRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Implementation of the UserDetailsService
 */
@Component
class UserDetailsServiceImpl(
    private val userRepo: UsersRepository,
    private val userRolesRepo: UserRolesRepository,
    private val userGroupsRepo: UserGroupsRepository
) : ReactiveUserDetailsService {

    @Suppress("EXPERIMENTAL_API_USAGE")
    override fun findByUsername(username: String): Mono<UserDetails> {
        return mono { userRepo.findByUsernameWithPassword(username) }
            .switchIfEmpty(Mono.error { UsernameNotFoundException("No matching user for $username") })
            .flatMap { user ->
                mono {
                    userRolesRepo.findRolesByUserId(user.id)
                        .onCompletion { emitAll(userGroupsRepo.findGroupRolesByUserId(user.id)) }
                        .toList()
                }.map { roles -> buildUserDetails(user, roles) }
            }
    }

    /**
     * Build a UserDetails for a User and its Roles
     * @param user the user
     * @param roles the user's roles
     * @return the UserDetails
     */
    private fun buildUserDetails(user: PrivateUser, roles: List<Role>): UserDetails {
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.username)
            .password(user.password)
            .authorities(roles.map { buildGrantedAuthority(it) })
            .build()
    }

    /**
     * Build a GrantedAuthority from a Role
     * @param role the role
     * @return the granted authority
     */
    private fun buildGrantedAuthority(role: Role): GrantedAuthority {
        return SimpleGrantedAuthority("ROLE_${role.name}")
    }
}
