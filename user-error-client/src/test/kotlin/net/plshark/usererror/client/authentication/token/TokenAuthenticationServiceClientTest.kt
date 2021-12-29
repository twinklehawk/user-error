package net.plshark.usererror.client.authentication.token

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.runBlocking
import net.plshark.usererror.authentication.AccountCredentials
import net.plshark.usererror.authentication.token.AuthToken
import net.plshark.usererror.client.UserErrorClientConfig
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

class TokenAuthenticationServiceClientTest {

    private val server = MockWebServer()
    private val webClient = WebClient.create()
    private val mapper = ObjectMapper().registerKotlinModule()
    private lateinit var client: TokenAuthenticationServiceClient

    @BeforeEach
    fun setup() {
        server.start()
        val config = UserErrorClientConfig(server.url("/").toString().dropLast(1))
        client = TokenAuthenticationServiceClient(webClient, config)
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
}
