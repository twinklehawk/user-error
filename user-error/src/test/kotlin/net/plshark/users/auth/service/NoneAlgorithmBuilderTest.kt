package net.plshark.users.auth.service

import net.plshark.users.auth.AuthProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class NoneAlgorithmBuilderTest {

    private val builder = NoneAlgorithmBuilder()

    @Test
    fun `none should load the none algorithm`() {
        val props = AuthProperties.forNone("bad-users", 1000)

        val algorithm = builder.build(props)!!

        assertEquals("none", algorithm.name)
    }

    @Test
    fun `should return null if the name is not none`() {
        val props = AuthProperties("bad", "bad-users", 1000, null, null, null)

        assertNull(builder.build(props))
    }
}
