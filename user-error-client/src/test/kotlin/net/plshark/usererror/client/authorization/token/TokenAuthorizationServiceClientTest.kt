package net.plshark.usererror.client.authorization.token

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.authorization.UserAuthorities
import net.plshark.usererror.client.UserErrorClientConfig
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

class TokenAuthorizationServiceClientTest {

    private val server = MockWebServer()
    private val webClient = WebClient.create()
    private val mapper = ObjectMapper().registerKotlinModule()
    private lateinit var client: TokenAuthorizationServiceClient

    @BeforeEach
    fun setup() {
        server.start()
        val config = UserErrorClientConfig(server.url("/").toString().dropLast(1))
        client = TokenAuthorizationServiceClient(webClient, config)
    }

    @AfterEach
    fun shutdown() {
        server.shutdown()
    }

    @Test
    fun `validateToken should return the user associated with an access token`() = runBlocking {
        val user = UserAuthorities("test-user", setOf("role1", "role2"))
        server.enqueue(
            MockResponse()
                .setBody(mapper.writeValueAsString(user))
                .setHeader("Content-Type", "application/json")
        )

        assertEquals(user, client.getAuthoritiesForToken("access-token"))

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/authorization/authorities", request.path)
        assertEquals("text/plain", request.getHeader("Content-Type"))
        assertEquals("access-token", request.body.readUtf8())
    }
}
