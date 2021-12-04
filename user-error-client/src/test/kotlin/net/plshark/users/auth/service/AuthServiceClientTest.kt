package net.plshark.users.auth.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.runBlocking
import net.plshark.users.auth.model.AccountCredentials
import net.plshark.users.auth.model.AuthToken
import net.plshark.users.auth.model.AuthenticatedUser
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

class AuthServiceClientTest {

    private val server = MockWebServer()
    private val webClient = WebClient.create()
    private val mapper = ObjectMapper().registerKotlinModule()
    private lateinit var client: AuthServiceClient

    @BeforeEach
    fun setup() {
        server.start()
        client = AuthServiceClient(webClient, server.url("/").toString().dropLast(1))
    }

    @AfterEach
    fun shutdown() {
        server.shutdown()
    }

    @Test
    fun `authenticate should return an auth token`() = runBlocking {
        val authToken = AuthToken(
            accessToken = "access",
            tokenType = "bearer",
            expiresIn = 500,
            refreshToken = "refresh",
            scope = "scope"
        )
        server.enqueue(
            MockResponse()
                .setBody(mapper.writeValueAsString(authToken))
                .setHeader("Content-Type", "application/json")
        )

        val credentials = AccountCredentials("username", "password")
        assertEquals(authToken, client.authenticate(credentials))

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/auth", request.path)
        assertEquals("application/json", request.getHeader("Content-Type"))
        assertEquals(credentials, mapper.readValue(request.body.readUtf8(), AccountCredentials::class.java))
    }

    @Test
    fun `refresh should return a new auth token`() = runBlocking {
        val authToken = AuthToken(
            accessToken = "access",
            tokenType = "bearer",
            expiresIn = 500,
            refreshToken = "refresh",
            scope = "scope"
        )
        server.enqueue(
            MockResponse()
                .setBody(mapper.writeValueAsString(authToken))
                .setHeader("Content-Type", "application/json")
        )

        assertEquals(authToken, client.refresh("refresh-token"))

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/auth/refresh", request.path)
        assertEquals("text/plain", request.getHeader("Content-Type"))
        assertEquals("refresh-token", request.body.readUtf8())
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
