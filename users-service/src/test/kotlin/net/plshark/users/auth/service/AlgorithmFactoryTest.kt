package net.plshark.users.auth.service

import net.plshark.users.auth.AuthProperties
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AlgorithmFactoryTest {

    private val factory = AlgorithmFactory(listOf())

    @Test
    fun `should throw an IllegalArgumentException if the algorithm name is not recognized`() {
        val props = AuthProperties.forSecret("made up name", "bad-users", 1000, "secret")

        assertThrows<IllegalArgumentException> {
            factory.buildAlgorithm(props)
        }
    }
}
