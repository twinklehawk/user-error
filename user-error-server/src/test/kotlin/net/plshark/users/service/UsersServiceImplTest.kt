package net.plshark.users.service

import io.mockk.every
import io.mockk.mockk
import net.plshark.errors.BadRequestException
import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Group
import net.plshark.users.model.Role
import net.plshark.users.model.User
import net.plshark.users.model.UserCreate
import net.plshark.users.repo.UserGroupsRepository
import net.plshark.users.repo.UserRolesRepository
import net.plshark.users.repo.UsersRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe

class UsersServiceImplTest {

    private val userRepo = mockk<UsersRepository>()
    private val userRolesRepo = mockk<UserRolesRepository>()
    private val userGroupsRepo = mockk<UserGroupsRepository>()
    private val encoder = mockk<PasswordEncoder>()
    private val service = UsersServiceImpl(userRepo, userRolesRepo, userGroupsRepo, encoder)

    @Test
    fun `new users have password encoded`() {
        every { encoder.encode("pass") } returns "pass-encoded"
        every { userRepo.insert(UserCreate("user", "pass-encoded")) } returns
                Mono.just(User(1, "user"))

        StepVerifier.create(service.create(UserCreate("user", "pass")))
            .expectNext(User(1, "user"))
            .verifyComplete()
    }

    @Test
    fun `create should map the exception for a duplicate username to a DuplicateException`() {
        val request = UserCreate("app", "pass")
        every { encoder.encode("pass") } returns "pass"
        every { userRepo.insert(request) } returns Mono.error(DataIntegrityViolationException("test error"))

        StepVerifier.create(service.create(request))
            .verifyError(DuplicateException::class.java)
    }

    @Test
    fun `cannot update a user to have an empty password`() {
        assertThrows<IllegalArgumentException> { service.updateUserPassword(123, "current", "") }
    }

    @Test
    fun `new password is encoded when updating password`() {
        every { encoder.encode("current") } returns "current-encoded"
        every { encoder.encode("new-pass") } returns "new-pass-encoded"
        every { userRepo.findById(100) } returns Mono.just(User(100, "test"))
        val probe = PublisherProbe.empty<Void>()
        every { userRepo.updatePassword(100, "current-encoded", "new-pass-encoded") } returns probe.mono()

        StepVerifier.create(service.updateUserPassword(100, "current", "new-pass"))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `no matching user when updating password throws exception`() {
        every { encoder.encode("current") } returns "current-encoded"
        every { encoder.encode("new") } returns "new-encoded"
        every { userRepo.findById(200) } returns Mono.empty()

        StepVerifier.create(service.updateUserPassword(200, "current", "new"))
            .verifyError(ObjectNotFoundException::class.java)
    }

    @Test
    fun `no matching existing password when updating password throws exception`() {
        every { encoder.encode("current") } returns "current-encoded"
        every { encoder.encode("new") } returns "new-encoded"
        every { userRepo.findById(100) } returns Mono.just(User(100, "ted"))
        every { userRepo.updatePassword(100, "current-encoded", "new-encoded") } returns Mono.error(
            EmptyResultDataAccessException(1)
        )

        StepVerifier.create(service.updateUserPassword(100, "current", "new"))
            .verifyError(BadRequestException::class.java)
    }

    @Test
    fun `the user is removed when the user is deleted`() {
        val userProbe = PublisherProbe.empty<Void>()
        every { userRepo.deleteById(100) } returns userProbe.mono()

        StepVerifier.create(service.deleteById(100))
            .verifyComplete()
        userProbe.assertWasSubscribed()
    }

    @Test
    fun `deleting by username deletes the user`() {
        val userProbe = PublisherProbe.empty<Void>()
        every { userRepo.deleteById(100) } returns userProbe.mono()

        StepVerifier.create(service.deleteById(100))
            .verifyComplete()
        userProbe.assertWasSubscribed()
    }

    @Test
    fun `getUsers should return all results`() {
        val user1 = User(1, "user")
        val user2 = User(2, "user2")
        every { userRepo.getAll(5, 0) } returns Flux.just(user1, user2)

        StepVerifier.create(service.getUsers(5, 0))
            .expectNext(user1, user2)
            .verifyComplete()
    }

    @Test
    fun `getUserRoles should return all roles`() {
        val r1 = Role(1, 2, "role1")
        val r2 = Role(2, 2, "role2")
        every { userRepo.findById(3) } returns User(3, "test-user").toMono()
        every { userRolesRepo.findRolesByUserId(3) } returns Flux.just(r1, r2)

        service.getUserRoles(3).test()
            .expectNext(r1, r2)
            .verifyComplete()
    }

    @Test
    fun `updateUserRoles passes through`() {
        val r1 = Role(1, 2, "role1")
        val r2 = Role(2, 2, "role2")
        val r3 = Role(3, 1, "role3")
        every { userRepo.findById(123) } returns User(123, "test-user").toMono()
        every { userRolesRepo.findRolesByUserId(123) } returns Flux.just(r1, r2)
        every { userRolesRepo.deleteById(123, 2) } returns Mono.empty()
        every { userRolesRepo.insert(123, 3) } returns Mono.empty()

        service.updateUserRoles(123, setOf(r1, r3)).test()
            .expectNext(r1, r3)
            .verifyComplete()
    }

    @Test
    fun `getUserGroups passes through`() {
        val g1 = Group(1, "group1")
        val g2 = Group(2, "group2")
        every { userRepo.findById(4) } returns User(4, "test-user").toMono()
        every { userGroupsRepo.findGroupsByUserId(4) } returns Flux.just(g1, g2)

        service.getUserGroups(4).test()
            .expectNext(g1, g2)
            .verifyComplete()
    }

    @Test
    fun `updateUserGroups passes through`() {
        val g1 = Group(1, "group1")
        val g2 = Group(2, "group2")
        val g3 = Group(3, "group3")
        every { userRepo.findById(123) } returns User(123, "test-user").toMono()
        every { userGroupsRepo.findGroupsByUserId(123) } returns Flux.just(g1, g2)
        every { userGroupsRepo.deleteById(123, 2) } returns Mono.empty()
        every { userGroupsRepo.insert(123, 3) } returns Mono.empty()

        service.updateUserGroups(123, setOf(g1, g3)).test()
            .expectNext(g1, g3)
            .verifyComplete()
    }
}
