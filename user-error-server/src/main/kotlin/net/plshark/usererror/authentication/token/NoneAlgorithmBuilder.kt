package net.plshark.usererror.authentication.token

import com.auth0.jwt.algorithms.Algorithm
import org.springframework.stereotype.Component

/**
 * Builder for the NONE algorithm
 */
@Component
class NoneAlgorithmBuilder : AlgorithmBuilder {

    override fun build(authProperties: AuthProperties): Algorithm? {
        var algorithm: Algorithm? = null
        val name: String = authProperties.algorithm
        if (NONE == name) algorithm = Algorithm.none()
        return algorithm
    }

    companion object {
        const val NONE = "none"
    }
}
