package net.plshark.users.webservice

import io.mockk.every
import io.mockk.mockk
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Application
import net.plshark.users.model.ApplicationCreate
import net.plshark.users.service.ApplicationsService
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class ApplicationsControllerTest {

    private val applicationsService = mockk<ApplicationsService>()
    private val controller = ApplicationsController(applicationsService)

    @Test
    fun `getting an application should pass through whatever the service returns`() {
        val app = Application(123, "test-app")
        every { applicationsService.findById(123) } returns Mono.just(app)

        StepVerifier.create(controller.findById(123))
                .expectNext(app)
                .verifyComplete()
    }

    @Test
    fun `getting an application should throw an exception when the application does not exist`() {
        every { applicationsService.findById(456) } returns Mono.empty()

        StepVerifier.create(controller.findById(456))
                .verifyError(ObjectNotFoundException::class.java)
    }

    @Test
    fun `getting all applications should pass through whatever the service returns`() {
        val app1 = Application(1, "app1")
        val app2 = Application(2, "app2")
        every { applicationsService.getApplications(100, 0) } returns Flux.just(app1, app2)

        StepVerifier.create(controller.getApplications(100, 0))
                .expectNext(app1)
                .expectNext(app2)
                .verifyComplete()
    }

    @Test
    fun `inserting should pass through whatever the service returns`() {
        val request = ApplicationCreate("name")
        val created = Application(1, "name")
        every { applicationsService.create(request) } returns Mono.just(created)

        StepVerifier.create(controller.create(request))
                .expectNext(created)
                .verifyComplete()
    }

    @Test
    fun `deleting should complete when the service completes`() {
        every { applicationsService.deleteById(789) } returns Mono.empty()

        StepVerifier.create(controller.delete(789))
                .verifyComplete()
    }
}
