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
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe

class UsersServiceImplTest {

    private val userRepo = mockk<UsersRepository>()
    private val userRolesRepo = mockk<UserRolesRepository>()
    private val userGroupsRepo = mockk<UserGroupsRepository>()
    private val rolesService = mockk<RolesService>()
    private val groupsService = mockk<GroupsService>()
    private val encoder = mockk<PasswordEncoder>()
    private val service = UsersServiceImpl(userRepo, userRolesRepo, userGroupsRepo, rolesService,
            groupsService, encoder)

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
        assertThrows<IllegalArgumentException> { service.updateUserPassword("bill", "current", "") }
    }

    @Test
    fun `new password is encoded when updating password`() {
        every { encoder.encode("current") } returns "current-encoded"
        every { encoder.encode("new-pass") } returns "new-pass-encoded"
        every { userRepo.getForUsername("ted") } returns Mono.just(User(100, "test"))
        val probe = PublisherProbe.empty<Void>()
        every { userRepo.updatePassword(100, "current-encoded", "new-pass-encoded") } returns probe.mono()

        StepVerifier.create(service.updateUserPassword("ted", "current", "new-pass"))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `no matching user when updating password throws exception`() {
        every { encoder.encode("current") } returns "current-encoded"
        every { encoder.encode("new") } returns "new-encoded"
        every { userRepo.getForUsername("ted") } returns Mono.empty()

        StepVerifier.create(service.updateUserPassword("ted", "current", "new"))
            .verifyError(ObjectNotFoundException::class.java)
    }

    @Test
    fun `no matching existing password when updating password throws exception`() {
        every { encoder.encode("current") } returns "current-encoded"
        every { encoder.encode("new") } returns "new-encoded"
        every { userRepo.getForUsername("ted") } returns Mono.just(User(100, "ted"))
        every { userRepo.updatePassword(100, "current-encoded", "new-encoded") } returns Mono.error(EmptyResultDataAccessException(1))

        StepVerifier.create(service.updateUserPassword("ted", "current", "new"))
                .verifyError(BadRequestException::class.java)
    }

    @Test
    fun `the user is removed when the user is deleted`() {
        val userProbe = PublisherProbe.empty<Void>()
        every { userRepo.delete(100) } returns userProbe.mono()

        StepVerifier.create(service.delete(100))
            .verifyComplete()
        userProbe.assertWasSubscribed()
    }

    @Test
    fun `deleting by username retrieves the user and deletes the user`() {
        every { userRepo.getForUsername("user") } returns Mono.just(User(100, "user"))
        val userProbe = PublisherProbe.empty<Void>()
        every { userRepo.delete(100) } returns userProbe.mono()

        StepVerifier.create(service.delete(100))
                .verifyComplete()
        userProbe.assertWasSubscribed()
    }

    @Test
    fun `granting a role to a user should add the role to the user's roles`() {
        every { userRepo.getForUsername("bill") } returns Mono.just(User(12, "bill"))
        every { rolesService.getRequired("app", "role") } returns Mono.just(Role(34, 1, "role"))
        val probe = PublisherProbe.empty<Void>()
        every { userRolesRepo.insert(12, 34) } returns probe.mono()

        StepVerifier.create(service.grantRoleToUser("bill", "app", "role"))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `removing a role from a user should remove the role from the user's roles`() {
        every { userRepo.getForUsername("ted") } returns Mono.just(User(100, "bill"))
        every { rolesService.getRequired("app", "role") } returns Mono.just(Role(200, 1, "role"))
        val probe = PublisherProbe.empty<Void>()
        every { userRolesRepo.delete(100, 200) } returns probe.mono()

        StepVerifier.create(service.removeRoleFromUser("ted", "app", "role"))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
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
    fun `should be able to add a user to a group`() {
        every { userRepo.getForUsername("bill") } returns Mono.just(User(100, "bill"))
        every { groupsService.getRequired("group") } returns Mono.just(Group(200, "group"))
        every { userGroupsRepo.insert(100, 200) } returns Mono.empty()

        StepVerifier.create(service.grantGroupToUser("bill", "group"))
                .verifyComplete()
    }

    @Test
    fun `should be able to remove a user from a group`() {
        every { userRepo.getForUsername("ted") } returns Mono.just(User(100, "ted"))
        every { groupsService.getRequired("group") } returns Mono.just(Group(200, "group"))
        every { userGroupsRepo.delete(100, 200) } returns Mono.empty()

        StepVerifier.create(service.removeGroupFromUser("ted", "group"))
                .verifyComplete()
    }
}
