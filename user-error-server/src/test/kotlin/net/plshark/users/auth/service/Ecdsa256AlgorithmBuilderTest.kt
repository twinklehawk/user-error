package net.plshark.users.auth.service

import net.plshark.users.auth.AuthProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class Ecdsa256AlgorithmBuilderTest {

    private val builder = Ecdsa256AlgorithmBuilder()

    @Test
    fun `should load the keystore from the filesystem and access the correct key for ECDSA256`() {
        val location = Ecdsa256AlgorithmBuilderTest::class.java.getResource("test-store.jks").file
        val props = AuthProperties.forKeystore(Ecdsa256AlgorithmBuilder.ECDSA256, "bad-users", 1000,
                AuthProperties.Keystore("pkcs12", location, "test-pass"),
                AuthProperties.Key("test-key", "test-pass"))

        val algorithm = builder.build(props)!!

        assertEquals("ES256", algorithm.name)
    }

    @Test
    fun `should fail if the keystore is null`() {
        val props = AuthProperties(
            Ecdsa256AlgorithmBuilder.ECDSA256,
            "bad-users",
            1000,
            null,
            null,
            AuthProperties.Key("test-key", "test-pass"))

        assertThrows<IllegalStateException> { builder.build(props) }
    }

    @Test
    fun `should fail if the key is null`() {
        val location = Ecdsa256AlgorithmBuilderTest::class.java.getResource("test-store.jks").file
        val props = AuthProperties(
            Ecdsa256AlgorithmBuilder.ECDSA256,
            "bad-users",
            1000,
            null,
            AuthProperties.Keystore("pkcs12", location, "test-pass"),
            null)

        assertThrows<IllegalStateException> { builder.build(props) }
    }

    @Test
    fun `should return null if the name is anything else`() {
        val props = AuthProperties.forNone("bad-users", 1000)

        assertNull(builder.build(props))
    }
}
