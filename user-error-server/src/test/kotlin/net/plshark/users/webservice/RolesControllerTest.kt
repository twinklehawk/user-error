package net.plshark.users.webservice

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

@Suppress("ReactiveStreamsUnusedPublisher")
class RolesControllerTest {

    private val rolesRepo = mockk<RolesRepository>()
    private val controller = RolesController(rolesRepo)

    @Test
    fun `creating should save a role and return the saved role`() {
        val role = RoleCreate(123, "role")
        val inserted = Role(1, 123, "role")
        every { rolesRepo.insert(role) } returns Mono.just(inserted)

        StepVerifier.create(controller.create(123, "role"))
            .expectNext(inserted)
            .verifyComplete()
    }

    @Test
    fun `create should map the exception for a duplicate name to a DuplicateException`() {
        val request = RoleCreate(321, "app")
        every { rolesRepo.insert(request.copy(applicationId = 321)) } returns
                Mono.error(DataIntegrityViolationException("test error"))

        StepVerifier.create(controller.create(321, "app"))
            .verifyError(DuplicateException::class.java)
    }

    @Test
    fun `deleting a role should delete the role`() {
        val rolesProbe = PublisherProbe.empty<Void>()
        every { rolesRepo.deleteById(100) } returns rolesProbe.mono()

        StepVerifier.create(controller.delete(100))
            .verifyComplete()
        rolesProbe.assertWasSubscribed()
    }

    @Test
    fun `should be able to retrieve a role by ID`() {
        val role = Role(123, 132, "role-name")
        every { rolesRepo.findById(123) } returns Mono.just(role)

        StepVerifier.create(controller.findById(132, 123))
            .expectNext(role)
            .verifyComplete()
    }

    @Test
    fun `getting a role should throw an exception when the role does not exist`() {
        every { rolesRepo.findById(5) } returns Mono.empty()

        StepVerifier.create(controller.findById(4, 5))
            .verifyError(ObjectNotFoundException::class.java)
    }

    @Test
    fun `should be able to retrieve all roles`() {
        val role1 = Role(1, 2, "role1")
        val role2 = Role(2, 2, "role2")
        every { rolesRepo.findRolesByApplicationId(2) } returns Flux.just(role1, role2)

        StepVerifier.create(controller.findRolesByApplication(2, 100, 0))
            .expectNext(role1, role2)
            .verifyComplete()
    }
}
