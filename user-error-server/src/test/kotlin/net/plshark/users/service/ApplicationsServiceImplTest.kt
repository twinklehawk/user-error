package net.plshark.users.service

import io.mockk.every
import io.mockk.mockk
import net.plshark.errors.DuplicateException
import net.plshark.users.model.Application
import net.plshark.users.model.ApplicationCreate
import net.plshark.users.repo.ApplicationsRepository
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe

class ApplicationsServiceImplTest {

    private val appsRepo = mockk<ApplicationsRepository>()
    private val service = ApplicationsServiceImpl(appsRepo)

    @Test
    fun `get should pass through the response from the repo`() {
        val app = Application(1, "app")
        every { appsRepo.findById(1) } returns Mono.just(app)

        StepVerifier.create(service.findById(1))
                .expectNext(app)
                .verifyComplete()
    }

    @Test
    fun `create should pass through the response from the repo`() {
        val request = ApplicationCreate("app")
        val inserted = Application(1, "app")
        every { appsRepo.insert(request) } returns Mono.just(inserted)

        StepVerifier.create(service.create(request))
                .expectNext(inserted)
                .verifyComplete()
    }

    @Test
    fun `create should map the exception for a duplicate name to a DuplicateException`() {
        val request = ApplicationCreate("app")
        every { appsRepo.insert(request) } returns Mono.error(DataIntegrityViolationException("test error"))

        StepVerifier.create(service.create(request))
                .verifyError(DuplicateException::class.java)
    }

    @Test
    fun `delete should delete the app`() {
        val deleteAppProbe = PublisherProbe.empty<Void>()
        every { appsRepo.deleteById(1) } returns deleteAppProbe.mono()

        StepVerifier.create(service.deleteById(1))
                .verifyComplete()
        deleteAppProbe.assertWasSubscribed()
    }
}
