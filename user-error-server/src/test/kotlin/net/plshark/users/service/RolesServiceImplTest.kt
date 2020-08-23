package net.plshark.users.service

import io.mockk.every
import io.mockk.mockk
import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Role
import net.plshark.users.model.RoleCreate
import net.plshark.users.repo.RolesRepository
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe

class RolesServiceImplTest {

    private val rolesRepo = mockk<RolesRepository>()
    private val service = RolesServiceImpl(rolesRepo)

    @Test
    fun `creating should save a role and return the saved role`() {
        val role = RoleCreate(123, "role")
        val inserted = Role(1, 123, "role")
        every { rolesRepo.insert(role) } returns Mono.just(inserted)

        StepVerifier.create(service.create(role))
                .expectNext(inserted)
                .verifyComplete()
    }

    @Test
    fun `create should map the exception for a duplicate name to a DuplicateException`() {
        val request = RoleCreate(321, "app")
        every { rolesRepo.insert(request.copy(applicationId = 321)) } returns
                Mono.error(DataIntegrityViolationException("test error"))

        StepVerifier.create(service.create(request))
                .verifyError(DuplicateException::class.java)
    }

    @Test
    fun `deleting a role should delete the role`() {
        val rolesProbe = PublisherProbe.empty<Void>()
        every { rolesRepo.delete(100) } returns rolesProbe.mono()

        StepVerifier.create(service.delete(100))
                .verifyComplete()
        rolesProbe.assertWasSubscribed()
    }

    @Test
    fun `deleting a role by name should look up the role then delete the role`() {
        every { rolesRepo[123, "role"] } returns Mono.just(Role(456, 123, "role"))
        val rolesProbe = PublisherProbe.empty<Void>()
        every { rolesRepo.delete(456) } returns rolesProbe.mono()

        StepVerifier.create(service.delete(123, "role"))
                .verifyComplete()
        rolesProbe.assertWasSubscribed()
    }

    @Test
    fun `should be able to retrieve a role by name`() {
        val role = Role(123, 132, "role-name")
        every { rolesRepo[132, "role-name"] } returns Mono.just(role)

        StepVerifier.create(service[132, "role-name"])
                .expectNext(role)
                .verifyComplete()
        StepVerifier.create(service.getRequired(132, "role-name"))
                .expectNext(role)
                .verifyComplete()
    }

    @Test
    fun `should return an error when a required role does not exist`() {
        every { rolesRepo[132, "role-name"] } returns Mono.empty()

        StepVerifier.create(service.getRequired(132, "role-name"))
                .verifyError(ObjectNotFoundException::class.java)
    }

    @Test
    fun `should be able to retrieve all roles`() {
        val role1 = Role(1, 2, "role1")
        val role2 = Role(2, 3, "role2")
        every { rolesRepo.getRoles(100, 0) } returns Flux.just(role1, role2)

        StepVerifier.create(service.getRoles(100, 0))
                .expectNext(role1, role2)
                .verifyComplete()
    }
}
