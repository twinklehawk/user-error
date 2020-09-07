package net.plshark.users.webservice

import io.mockk.every
import io.mockk.mockk
import net.plshark.errors.BadRequestException
import net.plshark.users.model.Group
import net.plshark.users.model.PasswordChangeRequest
import net.plshark.users.model.Role
import net.plshark.users.model.User
import net.plshark.users.model.UserCreate
import net.plshark.users.service.UsersService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe

class UsersControllerTest {

    private val service = mockk<UsersService>()
    private val controller = UsersController(service)

    @Test
    fun `delete passes the user ID through to be deleted`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.deleteById(1) } returns probe.mono()

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

    @Test
    fun `getUserRoles passes through`() {
        val r1 = Role(1, 2, "role1")
        val r2 = Role(2, 2, "role2")
        every { service.getUserRoles(123) } returns Flux.just(r1, r2)

        controller.getUserRoles(123).test()
            .expectNext(r1, r2)
            .verifyComplete()
    }

    @Test
    fun `updateUserRoles passes through`() {
        val r1 = Role(1, 2, "role1")
        val r2 = Role(2, 2, "role2")
        every { service.updateUserRoles(123, setOf(r1, r2)) } returns Flux.just(r1, r2)

        controller.updateUserRoles(123, setOf(r1, r2)).test()
            .expectNext(r1, r2)
            .verifyComplete()
    }

    @Test
    fun `getUserGroups passes through`() {
        val g1 = Group(1, "group1")
        val g2 = Group(2, "group2")
        every { service.getUserGroups(321) } returns Flux.just(g1, g2)

        controller.getUserGroups(321).test()
            .expectNext(g1, g2)
            .verifyComplete()
    }

    @Test
    fun `updateUserGroups passes through`() {
        val g1 = Group(1, "group1")
        val g2 = Group(2, "group2")
        every { service.updateUserGroups(421, setOf(g1, g2)) } returns Flux.just(g1, g2)

        controller.updateUserGroups(421, setOf(g1, g2)).test()
            .expectNext(g1, g2)
            .verifyComplete()
    }
}
