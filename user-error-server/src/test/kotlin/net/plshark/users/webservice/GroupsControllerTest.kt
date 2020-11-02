package net.plshark.users.webservice

import io.mockk.every
import io.mockk.mockk
import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Group
import net.plshark.users.model.GroupCreate
import net.plshark.users.repo.GroupRolesRepository
import net.plshark.users.repo.GroupsRepository
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe

@Suppress("ReactiveStreamsUnusedPublisher")
class GroupsControllerTest {

    private val groupsRepo = mockk<GroupsRepository>()
    private val groupRolesRepo = mockk<GroupRolesRepository>()
    private val controller = GroupsController(groupsRepo, groupRolesRepo)

    @Test
    fun `should be able to retrieve groups by ID`() {
        every { groupsRepo.findById(123) } returns Mono.just(Group(123, "group-name"))

        StepVerifier.create(controller.findById(123))
            .expectNext(Group(123, "group-name"))
            .verifyComplete()
    }

    @Test
    fun `getting a group should throw an exception when the group does not exist`() {
        every { groupsRepo.findById(2) } returns Mono.empty()

        StepVerifier.create(controller.findById(2))
            .verifyError(ObjectNotFoundException::class.java)
    }

    /*@Test
    fun `should be able to retrieve all groups`() {
        every { groupsRepo.getGroups(100, 0) } returns Flux.just(
            Group(1, "group1"),
            Group(2, "group2"))

        StepVerifier.create(service.getGroups(100, 0))
            .expectNext(Group(1, "group1"), Group(2, "group2"))
            .verifyComplete()
    }*/

    @Test
    fun `creating should save and return the saved group`() {
        val request = GroupCreate("group")
        val inserted = Group(1, "group")
        every { groupsRepo.insert(request) } returns Mono.just(inserted)

        StepVerifier.create(controller.create(request))
            .expectNext(inserted)
            .verifyComplete()
    }

    @Test
    fun `create should map the exception for a duplicate name to a DuplicateException`() {
        val request = GroupCreate("app")
        every { groupsRepo.insert(request) } returns Mono.error(DataIntegrityViolationException("test error"))

        StepVerifier.create(controller.create(request))
            .verifyError(DuplicateException::class.java)
    }

    @Test
    fun `deleting should delete the group`() {
        val groupsProbe = PublisherProbe.empty<Void>()
        every { groupsRepo.deleteById(100) } returns groupsProbe.mono()

        StepVerifier.create(controller.delete(100))
            .verifyComplete()
        groupsProbe.assertWasSubscribed()
    }

    @Test
    fun `should be able to add a role to a group`() {
        val probe = PublisherProbe.empty<Void>()
        every { groupRolesRepo.insert(1, 2) } returns probe.mono()

        StepVerifier.create(controller.addRoleToGroup(1, 2))
            .verifyComplete()
        probe.assertWasSubscribed()
    }

    @Test
    fun `should be able to remove a role from a group`() {
        val probe = PublisherProbe.empty<Void>()
        every { groupRolesRepo.deleteById(1, 2) } returns probe.mono()

        StepVerifier.create(controller.removeRoleFromGroup(1, 2))
            .verifyComplete()
        probe.assertWasSubscribed()
    }
}
