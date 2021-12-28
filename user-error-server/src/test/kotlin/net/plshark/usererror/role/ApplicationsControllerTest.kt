package net.plshark.usererror.role

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.error.DuplicateException
import net.plshark.usererror.error.ObjectNotFoundException
import net.plshark.usererror.user.Application
import net.plshark.usererror.user.ApplicationCreate
import org.assertj.core.api.Assertions.assertThat
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

    @Test
    fun `getAll should return a page of results`() = runBlocking<Unit> {
        val app1 = Application(1, "app1")
        val app2 = Application(2, "app2")
        every { appsRepo.getAll(100, 0) } returns flowOf(app1, app2)

        assertThat(controller.getApplications(100, 0).toList())
            .hasSize(2).contains(app1, app2)
    }
}
