package net.plshark.users.auth.jwt

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class HttpBearerBuilderTest {

    private val authManager = mockk<JwtReactiveAuthenticationManager>()
    private val httpBearerBuilder = HttpBearerBuilder(authManager)

    @Test
    fun `buildFilter should return a filter`() = assertNotNull(httpBearerBuilder.buildFilter())

    @Test
    fun `buildEntryPoint should return an entry point`() = assertNotNull(httpBearerBuilder.buildEntryPoint())
}
