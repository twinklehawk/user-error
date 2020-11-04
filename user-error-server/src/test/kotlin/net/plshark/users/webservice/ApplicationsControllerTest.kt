package net.plshark.users.webservice

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Application
import net.plshark.users.model.ApplicationCreate
import net.plshark.users.repo.ApplicationsRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.DataIntegrityViolationException

@Suppress("ReactiveStreamsUnusedPublisher")
class ApplicationsControllerTest {

    private val appsRepo = mockk<ApplicationsRepository>()
    private val controller = ApplicationsController(appsRepo)

    @Test
    fun `get should pass through the response from the repo`() = runBlocking {
        val app = Application(1, "app")
        coEvery { appsRepo.findById(1) } returns app

        assertEquals(app, controller.findById(1))
    }

    @Test
    fun `getting an application should throw an exception when the application does not exist`() {
        coEvery { appsRepo.findById(456) } returns null

        assertThrows<ObjectNotFoundException> {
            runBlocking {
                controller.findById(456)
            }
        }
    }

    @Test
    fun `create should pass through the response from the repo`() = runBlocking {
        val request = ApplicationCreate("app")
        val inserted = Application(1, "app")
        coEvery { appsRepo.insert(request) } returns inserted

        assertEquals(inserted, controller.create(request))
    }

    @Test
    fun `create should map the exception for a duplicate name to a DuplicateException`() {
        val request = ApplicationCreate("app")
        coEvery { appsRepo.insert(request) } throws DataIntegrityViolationException("test error")

        assertThrows<DuplicateException> {
            runBlocking {
                controller.create(request)
            }
        }
    }

    @Test
    fun `delete should delete the app`() {
        coEvery { appsRepo.deleteById(1) } coAnswers { }
        runBlocking { controller.delete(1) }
        coVerify { appsRepo.deleteById(1) }
    }
}
