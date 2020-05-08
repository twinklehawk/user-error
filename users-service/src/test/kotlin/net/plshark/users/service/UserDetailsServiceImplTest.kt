package net.plshark.users.service

import io.mockk.every
import io.mockk.mockk
import net.plshark.users.model.Role
import net.plshark.users.model.User
import net.plshark.users.repo.UserGroupsRepository
import net.plshark.users.repo.UserRolesRepository
import net.plshark.users.repo.UsersRepository
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class UserDetailsServiceImplTest {

    private val usersRepo = mockk<UsersRepository>()
    private val userRolesRepo = mockk<UserRolesRepository>()
    private val userGroupsRepo = mockk<UserGroupsRepository>()
    private val service = UserDetailsServiceImpl(usersRepo, userRolesRepo, userGroupsRepo)

    @Test
    fun `a user and its roles are mapped to the correct UserDetails`() {
        every { usersRepo.getForUsernameWithPassword("user") } returns Mono.just(User(25, "user", "pass"))
        every { userRolesRepo.getRolesForUser(25) } returns Flux.just(
                Role(3, 1, "normal-user"),
                Role(5, 1, "admin"))
        every { userGroupsRepo.getGroupRolesForUser(25) } returns Flux.just(
                Role(3, 1, "group-role-1"),
                Role(5, 1, "group-role-2"))

        StepVerifier.create(service.findByUsername("user"))
            .expectNextMatches { details ->
                details.username == "user" &&
                        details.password == "pass" &&
                        details.authorities.size == 4 &&
                        details.authorities.contains(SimpleGrantedAuthority("ROLE_normal-user")) &&
                        details.authorities.contains(SimpleGrantedAuthority("ROLE_admin")) &&
                        details.authorities.contains(SimpleGrantedAuthority("ROLE_group-role-1")) &&
                        details.authorities.contains(SimpleGrantedAuthority("ROLE_group-role-2"))
            }
            .verifyComplete()
    }

    @Test
    fun `UsernameNotFoundException thrown when no user is found for username`() {
        every { usersRepo.getForUsernameWithPassword("user") } returns Mono.empty()

        StepVerifier.create(service.findByUsername("user"))
            .verifyError(UsernameNotFoundException::class.java)
    }

    @Test
    fun `empty roles returns no granted authorities`() {
        every { usersRepo.getForUsernameWithPassword("user") } returns Mono.just(User(25, "user", "pass"))
        every { userRolesRepo.getRolesForUser(25) } returns Flux.empty()
        every { userGroupsRepo.getGroupRolesForUser(25) } returns Flux.empty()

        StepVerifier.create(service.findByUsername("user"))
            .expectNextMatches { details -> details.authorities.isEmpty() }
            .verifyComplete()
    }
}
