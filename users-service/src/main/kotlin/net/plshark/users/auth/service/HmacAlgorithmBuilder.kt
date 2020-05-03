package net.plshark.users.auth.service

import com.auth0.jwt.algorithms.Algorithm
import net.plshark.users.auth.AuthProperties
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * Builder for HMAC algorithms
 */
@Component
class HmacAlgorithmBuilder : AlgorithmBuilder {

    override fun build(authProperties: AuthProperties): Algorithm? {
        var algorithm: Algorithm? = null
        val name: String = authProperties.algorithm

        if (HMAC256 == name || HMAC512 == name) {
            val secret: String = authProperties.secret!!
            check(StringUtils.hasLength(secret)) { "Must set a secret when using the HMAC256 algorithm" }
            algorithm = if (HMAC256 == name) Algorithm.HMAC256(secret) else if (HMAC512 == name) Algorithm.HMAC512(secret) else null
        }
        return algorithm
    }

    companion object {
        const val HMAC256 = "hmac256"
        const val HMAC512 = "hmac512"
    }
}
