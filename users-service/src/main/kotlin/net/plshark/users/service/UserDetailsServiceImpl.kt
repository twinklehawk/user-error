package net.plshark.users.service

import net.plshark.users.model.Role
import net.plshark.users.model.PrivateUser
import net.plshark.users.repo.UserGroupsRepository
import net.plshark.users.repo.UserRolesRepository
import net.plshark.users.repo.UsersRepository
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

    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepo.getForUsernameWithPassword(username)
            .switchIfEmpty(Mono.error { UsernameNotFoundException("No matching user for $username") })
            .flatMap { user ->
                userRolesRepo.getRolesForUser(user.id)
                    .mergeWith(userGroupsRepo.getGroupRolesForUser(user.id))
                    .collectList()
                    .map { roles -> buildUserDetails(user, roles) }
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