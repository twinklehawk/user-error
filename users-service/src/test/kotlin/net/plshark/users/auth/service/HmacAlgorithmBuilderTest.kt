package net.plshark.users.auth.service

import net.plshark.users.auth.AuthProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class HmacAlgorithmBuilderTest {

    private val builder = HmacAlgorithmBuilder()

    @Test
    fun `hmac256 should load the correct algorithm`() {
        val props = AuthProperties.forSecret(HmacAlgorithmBuilder.HMAC256, "bad-users", 1000, "secret")

        val algorithm = builder.build(props)!!

        assertEquals("HS256", algorithm.name)
    }

    @Test
    fun `hmac512 should load the correct algorithm`() {
        val props = AuthProperties.forSecret(HmacAlgorithmBuilder.HMAC512, "bad-users", 1000, "secret")

        val algorithm = builder.build(props)!!

        assertEquals("HS512", algorithm.name)
    }

    @Test
    fun `should fail if the secret is null`() {
        val props = AuthProperties(HmacAlgorithmBuilder.HMAC256, "bad-users", 1000, null, null, null)

        assertThrows<IllegalStateException> { builder.build(props) }
    }

    @Test
    fun `should return null if the name is anything else`() {
        val props = AuthProperties.forNone("bad-users", 1000)

        assertNull(builder.build(props))
    }
}
