package net.plshark.usererror.authorization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

class AuthorizationServiceClientTest {

    private val server = MockWebServer()
    private val webClient = WebClient.create()
    private val mapper = ObjectMapper().registerKotlinModule()
    private lateinit var client: AuthorizationServiceClient

    @BeforeEach
    fun setup() {
        server.start()
        client = AuthorizationServiceClient(webClient, server.url("/").toString().dropLast(1))
    }

    @AfterEach
    fun shutdown() {
        server.shutdown()
    }

    @Test
    fun `validateToken should return the user associated with an access token`() = runBlocking {
        val user = AuthenticatedUser("test-user", setOf("role1", "role2"))
        server.enqueue(
            MockResponse()
                .setBody(mapper.writeValueAsString(user))
                .setHeader("Content-Type", "application/json")
        )

        assertEquals(user, client.validateToken("access-token"))

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/auth/validate", request.path)
        assertEquals("text/plain", request.getHeader("Content-Type"))
        assertEquals("access-token", request.body.readUtf8())
    }
}
