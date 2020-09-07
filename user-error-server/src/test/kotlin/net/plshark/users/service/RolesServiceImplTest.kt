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
        every { rolesRepo.deleteById(100) } returns rolesProbe.mono()

        StepVerifier.create(service.deleteById(100))
                .verifyComplete()
        rolesProbe.assertWasSubscribed()
    }

    @Test
    fun `should be able to retrieve a role by ID`() {
        val role = Role(123, 132, "role-name")
        every { rolesRepo.findById(123) } returns Mono.just(role)

        StepVerifier.create(service.findById(123))
                .expectNext(role)
                .verifyComplete()
        StepVerifier.create(service.findRequiredById(123))
                .expectNext(role)
                .verifyComplete()
    }

    @Test
    fun `should return an error when a required role does not exist`() {
        every { rolesRepo.findById(132) } returns Mono.empty()

        StepVerifier.create(service.findRequiredById(132))
                .verifyError(ObjectNotFoundException::class.java)
    }

    @Test
    fun `should be able to retrieve all roles`() {
        val role1 = Role(1, 2, "role1")
        val role2 = Role(2, 2, "role2")
        every { rolesRepo.findRolesByApplicationId(2) } returns Flux.just(role1, role2)

        StepVerifier.create(service.findRolesByApplicationId(2, 100, 0))
                .expectNext(role1, role2)
                .verifyComplete()
    }
}
