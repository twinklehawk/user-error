package net.plshark.users.webservice

import io.mockk.every
import io.mockk.mockk
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Group
import net.plshark.users.model.GroupCreate
import net.plshark.users.service.GroupsService
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe

class GroupsControllerTest {

    private val groupsService = mockk<GroupsService>()
    private val controller = GroupsController(groupsService)

    @Test
    fun `get should pass through the response from the service`() {
        val group = Group(1, "group")
        every { groupsService.findById(1) } returns Mono.just(group)

        StepVerifier.create(controller.findById(1))
                .expectNext(group)
                .verifyComplete()
    }

    @Test
    fun `getting a group should throw an exception when the group does not exist`() {
        every { groupsService.findById(2) } returns Mono.empty()

        StepVerifier.create(controller.findById(2))
                .verifyError(ObjectNotFoundException::class.java)
    }

    @Test
    fun `insert should pass through the response from the service`() {
        val request = GroupCreate("group")
        val inserted = Group(1, "group")
        every { groupsService.create(request) } returns Mono.just(inserted)

        StepVerifier.create(controller.create(request))
                .expectNext(inserted)
                .verifyComplete()
    }

    @Test
    fun `delete should pass through the response from the service`() {
        val probe = PublisherProbe.empty<Void>()
        every { groupsService.deleteById(321) } returns probe.mono()

        StepVerifier.create(controller.delete(321))
                .verifyComplete()
        probe.assertWasSubscribed()
    }
}
