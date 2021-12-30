package net.plshark.usererror.server.authentication.token

import com.auth0.jwt.algorithms.Algorithm
import net.plshark.usererror.server.AuthProperties
import java.io.IOException
import java.security.GeneralSecurityException

/**
 * Builds an algorithm from the auth properties
 */
class AlgorithmFactory(private val algorithmBuilders: List<AlgorithmBuilder>) {

    /**
     * Build the algorithm configured in the auth properties
     * @param props the properties
     * @return the algorithm
     * @throws GeneralSecurityException if unable to load keys required by the algorithm
     * @throws IOException if unable to load files required by the algorithm
     * @throws IllegalArgumentException if the algorithm specified in the properties is invalid
     */
    @Throws(GeneralSecurityException::class, IOException::class)
    fun buildAlgorithm(props: AuthProperties): Algorithm {
        val name = props.algorithm
        var algorithm: Algorithm? = null
        for (builder in algorithmBuilders) {
            algorithm = builder.build(props)
            if (algorithm != null) break
        }
        requireNotNull(algorithm) { "Unsupported algorithm: $name" }
        return algorithm
    }
}
