package net.plshark.users.webservice

import io.mockk.every
import io.mockk.mockk
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Application
import net.plshark.users.model.Role
import net.plshark.users.model.RoleCreate
import net.plshark.users.service.ApplicationsService

import net.plshark.users.service.RolesService
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe

class RolesControllerTest {

    private val service = mockk<RolesService>()
    private val appService = mockk<ApplicationsService>()
    private val controller = RolesController(service, appService)

    @Test
    fun `insert passes role through to service`() {
        val inserted = Role(100, 12, "app")
        every { appService.findById(12) } returns Mono.just(Application(12, "app"))
        every { service.create(RoleCreate(12, "admin")) } returns Mono.just(inserted)

        StepVerifier.create(controller.create(12, "admin"))
            .expectNext(inserted)
            .verifyComplete()
    }

    @Test
    fun `insert returns an ObjectNotFoundException if the application is not found`() {
        every { appService.findById(4) } returns Mono.empty()

        StepVerifier.create(controller.create(4, "admin"))
            .verifyError(ObjectNotFoundException::class.java)
    }

    @Test
    fun `getting a role should throw an exception when the role does not exist`() {
        every { service.findById(5) } returns Mono.empty()

        StepVerifier.create(controller.findById(4, 5))
                .verifyError(ObjectNotFoundException::class.java)
    }

    @Test
    fun `delete passes ID through to service`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.deleteById(6) } returns probe.mono()

        StepVerifier.create(controller.delete(5, 6))
            .verifyComplete()
        probe.assertWasSubscribed()
    }

    @Test
    fun `findRolesByApplication passes through`() {
        val role1 = Role(1, 1, "role1")
        val role2 = Role(2, 1, "role2")
        every { service.findRolesByApplicationId(4, 3, 2) } returns Flux.just(role1, role2)

        StepVerifier.create(controller.findRolesByApplication(4, 3, 2))
                .expectNext(role1, role2)
                .verifyComplete()
    }

    @Test
    fun `get passes the role name through`() {
        val role1 = Role(1, 1, "role")
        every { service.findById(1) } returns Mono.just(role1)

        StepVerifier.create(controller.findById(1, 1))
                .expectNext(role1)
                .verifyComplete()
    }
}
