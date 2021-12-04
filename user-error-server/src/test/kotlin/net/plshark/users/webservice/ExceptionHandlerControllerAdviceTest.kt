package net.plshark.users.webservice

import io.mockk.every
import io.mockk.mockk
import net.plshark.errors.BadRequestException
import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import java.net.URI

class ExceptionHandlerControllerAdviceTest {

    private val request = mockk<ServerHttpRequest>()
    private val advice = ExceptionHandlerControllerAdvice()

    @BeforeEach
    fun setup() {
        every { request.uri } returns URI.create("http://test/url")
    }

    @Test
    fun `bad request builds correct response body`() {
        val response = advice.handleBadRequest(BadRequestException("bad request"), request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("bad request", response.body?.message)
        assertEquals("http://test/url", response.body?.path)
        assertEquals(400, response.body?.status)
        assertEquals(HttpStatus.BAD_REQUEST.reasonPhrase, response.body?.statusDetail)
        assertNotNull(response.body?.timestamp)
    }

    @Test
    fun `object not found builds correct response body`() {
        val response = advice.handleObjectNotFound(ObjectNotFoundException("not found"), request)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("not found", response.body?.message)
        assertEquals("http://test/url", response.body?.path)
        assertEquals(404, response.body?.status)
        assertEquals(HttpStatus.NOT_FOUND.reasonPhrase, response.body?.statusDetail)
        assertNotNull(response.body?.timestamp)
    }

    @Test
    fun `duplicate builds correct response body`() {
        val response = advice.handleDuplicate(DuplicateException("duplicate name"), request)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("duplicate name", response.body?.message)
        assertEquals("http://test/url", response.body?.path)
        assertEquals(409, response.body?.status)
        assertEquals(HttpStatus.CONFLICT.reasonPhrase, response.body?.statusDetail)
        assertNotNull(response.body?.timestamp)
    }
}
