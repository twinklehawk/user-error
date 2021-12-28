package net.plshark.usererror.user

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.role.Role
import net.plshark.usererror.role.UserGroupsRepository
import net.plshark.usererror.role.UserRolesRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import reactor.test.StepVerifier

class UserDetailsServiceImplTest {

    private val usersRepo = mockk<UsersRepository>()
    private val userRolesRepo = mockk<UserRolesRepository>()
    private val userGroupsRepo = mockk<UserGroupsRepository>()
    private val service = UserDetailsServiceImpl(usersRepo, userRolesRepo, userGroupsRepo)

    @Test
    fun `a user and its roles are mapped to the correct UserDetails`() = runBlocking<Unit> {
        coEvery { usersRepo.findByUsernameWithPassword("user") } returns
            PrivateUser(25, "user", "pass")
        every { userRolesRepo.findRolesByUserId(25) } returns flow {
            emit(Role(3, 1, "normal-user"))
            emit(Role(5, 1, "admin"))
        }
        every { userGroupsRepo.findGroupRolesByUserId(25) } returns flow {
            emit(Role(3, 1, "group-role-1"))
            emit(Role(5, 1, "group-role-2"))
        }

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
        coEvery { usersRepo.findByUsernameWithPassword("user") } returns null

        assertThrows<UsernameNotFoundException> {
            service.findByUsername("user").block()
        }
    }

    @Test
    fun `empty roles returns no granted authorities`() = runBlocking<Unit> {
        coEvery { usersRepo.findByUsernameWithPassword("user") } returns PrivateUser(
            25,
            "user",
            "pass"
        )
        every { userRolesRepo.findRolesByUserId(25) } returns flow { }
        every { userGroupsRepo.findGroupRolesByUserId(25) } returns flow { }

        StepVerifier.create(service.findByUsername("user"))
            .expectNextMatches { details -> details.authorities.isEmpty() }
            .verifyComplete()
    }
}
