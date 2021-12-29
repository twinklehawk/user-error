package net.plshark.usererror.authentication.token.spring

import io.mockk.mockk
import net.plshark.usererror.authorization.token.TokenAuthorizationService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class HttpBearerBuilderTest {

    private val authService = mockk<TokenAuthorizationService>()
    private val httpBearerBuilder = HttpBearerBuilder(authService)

    @Test
    fun `buildFilter should return a filter`() = assertNotNull(httpBearerBuilder.buildFilter())
}
