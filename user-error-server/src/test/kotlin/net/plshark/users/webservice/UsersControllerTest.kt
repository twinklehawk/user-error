package net.plshark.users.webservice

import io.mockk.every
import io.mockk.mockk
import net.plshark.errors.BadRequestException
import net.plshark.users.model.PasswordChangeRequest
import net.plshark.users.model.RoleGrant
import net.plshark.users.model.User
import net.plshark.users.model.UserCreate
import net.plshark.users.service.UsersService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe

class UsersControllerTest {

    private val service = mockk<UsersService>()
    private val controller = UsersController(service)

    @Test
    fun `delete passes the user ID through to be deleted`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.delete(1) } returns probe.mono()

        StepVerifier.create(controller.delete(1))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `change password passes the user ID, current password, and new passwords through`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.updateUserPassword(2, "current", "new") } returns probe.mono()

        StepVerifier.create(controller.changePassword(2, PasswordChangeRequest("current", "new")))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `granting a role passes the user and role IDs through`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.grantRoleToUser(3, 43, 65) } returns probe.mono()

        StepVerifier.create(controller.grantRole(3, RoleGrant(43, 65)))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `removing a role passes the user and role IDs through`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.removeRoleFromUser(4, 56, 45) } returns probe.mono()

        StepVerifier.create(controller.removeRole(4, 56, 45))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `granting a group passes the user and group IDs through`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.grantGroupToUser(5, 3) } returns probe.mono()

        StepVerifier.create(controller.grantGroup(5, 3))
                .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `removing a group passes the user name group IDs through`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.removeGroupFromUser(6, 4) } returns probe.mono()

        StepVerifier.create(controller.removeGroup(6, 4))
                .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `getUsers passes the max results and offset through`() {
        val user1 = User(1, "user")
        val user2 = User(2, "user2")
        every { service.getUsers(3, 2) } returns Flux.just(user1, user2)

        StepVerifier.create(controller.getUsers(3, 2))
                .expectNext(user1, user2)
                .verifyComplete()
    }

    @Test
    fun `getUser passes the username through`() {
        val user1 = User(1, "user")
        every { service.findById(7) } returns Mono.just(user1)

        StepVerifier.create(controller.findById(7))
                .expectNext(user1)
                .verifyComplete()
    }

    @Test
    fun `insert passes through the response from the service`() {
        val request = UserCreate("user", "test-pass")
        val created = User(1, "user")
        every { service.create(request) } returns Mono.just(created)

        StepVerifier.create(controller.create(request))
                .expectNext(created)
                .verifyComplete()
    }

    @Test
    fun `an insert request is rejected if the password is empty`() {
        val request = UserCreate("user", "")

        assertThrows<BadRequestException> { controller.create(request) }
    }
}
