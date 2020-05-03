package net.plshark.users.service

import io.mockk.every
import io.mockk.mockk
import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Application
import net.plshark.users.model.Role
import net.plshark.users.repo.ApplicationsRepository
import net.plshark.users.repo.RolesRepository
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe

class RolesServiceImplTest {

    private val rolesRepo = mockk<RolesRepository>()
    private val appsRepo = mockk<ApplicationsRepository>()
    private val service = RolesServiceImpl(rolesRepo, appsRepo)

    @Test
    fun `creating should save a role and return the saved role`() {
        val role = Role(null, 123, "role")
        val inserted = role.copy(id = 1)
        every { rolesRepo.insert(role) } returns Mono.just(inserted)

        StepVerifier.create(service.create(role))
                .expectNext(inserted)
                .verifyComplete()
    }

    @Test
    fun `creating with an application name should look up the application and set the ID before inserting`() {
        val inserted = Role(1, 321, "role")
        every { appsRepo.get("app") } returns Mono.just(Application(321, "app"))
        every { rolesRepo.insert(Role(null, 321, "role")) } returns Mono.just(inserted)

        StepVerifier.create(service.create("app", Role(null, null, "role")))
                .expectNext(inserted)
                .verifyComplete()
    }

    @Test
    fun `create should map the exception for a duplicate name to a DuplicateException`() {
        val request = Role(null, null, "app")
        every { appsRepo.get("app") } returns Mono.just(Application(321, "app"))
        every { rolesRepo.insert(request.copy(applicationId = 321)) } returns
                Mono.error(DataIntegrityViolationException("test error"))

        StepVerifier.create(service.create("app", request))
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
        every { appsRepo.get("app") } returns Mono.just(Application(123, "app"))
        every { rolesRepo.get(123, "role") } returns Mono.just(Role(456, 123, "role"))
        val rolesProbe = PublisherProbe.empty<Void>()
        every { rolesRepo.delete(456) } returns rolesProbe.mono()

        StepVerifier.create(service.delete("app", "role"))
                .verifyComplete()
        rolesProbe.assertWasSubscribed()
    }

    @Test
    fun `should be able to retrieve a role by name`() {
        every { appsRepo.get("app-name") } returns Mono.just(Application(132, "app-name"))
        val role = Role(123, 132, "role-name")
        every { rolesRepo.get(132, "role-name") } returns Mono.just(role)

        StepVerifier.create(service.get("app-name", "role-name"))
                .expectNext(role)
                .verifyComplete()
        StepVerifier.create(service.getRequired("app-name", "role-name"))
                .expectNext(role)
                .verifyComplete()
    }

    @Test
    fun `should return an error when a required role's application does not exist`() {
        every { appsRepo.get("app-name") } returns Mono.empty()

        StepVerifier.create(service.getRequired("app-name", "role-name"))
                .verifyError(ObjectNotFoundException::class.java)
    }

    @Test
    fun `should return an error when a required role does not exist`() {
        every { appsRepo.get("app-name") } returns Mono.just(Application(132, "app-name"))
        every { rolesRepo.get(132, "role-name") } returns Mono.empty()

        StepVerifier.create(service.getRequired("app-name", "role-name"))
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
