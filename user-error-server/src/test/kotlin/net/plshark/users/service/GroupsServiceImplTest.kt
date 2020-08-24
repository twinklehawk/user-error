package net.plshark.users.service

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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe

class GroupsServiceImplTest {

    private val groupsRepo = mockk<GroupsRepository>()
    private val groupRolesRepo = mockk<GroupRolesRepository>()
    private val service = GroupsServiceImpl(groupsRepo, groupRolesRepo)

    @Test
    fun `should be able to retrieve groups by ID`() {
        every { groupsRepo.findById(123) } returns Mono.just(Group(123, "group-name"))

        StepVerifier.create(service.findById(123))
                .expectNext(Group(123, "group-name"))
                .verifyComplete()
        StepVerifier.create(service.findRequiredById(123))
                .expectNext(Group(123, "group-name"))
                .verifyComplete()
    }

    @Test
    fun `should throw an exception when a required group is not found`() {
        every { groupsRepo.findById(123) } returns Mono.empty()

        StepVerifier.create(service.findRequiredById(123))
                .verifyError(ObjectNotFoundException::class.java)
    }

    @Test
    fun `should be able to retrieve all groups`() {
        every { groupsRepo.getGroups(100, 0) } returns Flux.just(
                Group(1, "group1"),
                Group(2, "group2"))

        StepVerifier.create(service.getGroups(100, 0))
                .expectNext(Group(1, "group1"), Group(2, "group2"))
                .verifyComplete()
    }

    @Test
    fun `creating should save and return the saved group`() {
        val request = GroupCreate("group")
        val inserted = Group(1, "group")
        every { groupsRepo.insert(request) } returns Mono.just(inserted)

        StepVerifier.create(service.create(request))
                .expectNext(inserted)
                .verifyComplete()
    }

    @Test
    fun `create should map the exception for a duplicate name to a DuplicateException`() {
        val request = GroupCreate("app")
        every { groupsRepo.insert(request) } returns Mono.error(DataIntegrityViolationException("test error"))

        StepVerifier.create(service.create(request))
                .verifyError(DuplicateException::class.java)
    }

    @Test
    fun `deleting should delete the group`() {
        val groupsProbe = PublisherProbe.empty<Void>()
        every { groupsRepo.delete(100) } returns groupsProbe.mono()

        StepVerifier.create(service.delete(100))
                .verifyComplete()
        groupsProbe.assertWasSubscribed()
    }

    @Test
    fun `deleting by name should delete the group`() {
        every { groupsRepo.findByName("group") } returns Mono.just(Group(100, "group"))
        val groupsProbe = PublisherProbe.empty<Void>()
        every { groupsRepo.delete(100) } returns groupsProbe.mono()

        StepVerifier.create(service.delete("group"))
                .verifyComplete()
        groupsProbe.assertWasSubscribed()
    }

    @Test
    fun `should be able to add a role to a group`() {
        val probe = PublisherProbe.empty<Void>()
        every { groupRolesRepo.insert(1, 2) } returns probe.mono()

        StepVerifier.create(service.addRoleToGroup(1, 2))
                .verifyComplete()
        probe.assertWasSubscribed()
    }

    @Test
    fun `should be able to remove a role from a group`() {
        val probe = PublisherProbe.empty<Void>()
        every { groupRolesRepo.delete(1, 2) } returns probe.mono()

        StepVerifier.create(service.removeRoleFromGroup(1, 2))
                .verifyComplete()
        probe.assertWasSubscribed()
    }
}
