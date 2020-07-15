package net.plshark.users.auth.throttle.impl

import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.exceptions.JWTVerificationException
import net.plshark.users.auth.throttle.UsernameExtractor
import org.slf4j.LoggerFactory
import org.springframework.http.server.reactive.ServerHttpRequest

/**
 * Username extractor for requests using bearer authentication with JWT
 */
class JwtUsernameExtractor(private val verifier: JWTVerifier) : UsernameExtractor {

    override fun extractUsername(request: ServerHttpRequest): String? {
        return request.headers.getFirst("Authorization")?.let { this.extractUsername(it) }
    }

    /**
     * Extract the username from an authorization header value
     * @param header the header value, preferably a valid JWT
     * @return the username
     */
    private fun extractUsername(header: String): String? {
        var username: String? = null
        if (header.startsWith(AUTHORIZATION_TYPE)) {
            try {
                username = verifier.verify(header.substring(AUTHORIZATION_TYPE.length)).subject
            } catch (e: JWTVerificationException) {
                log.debug("Invalid JWT token", e)
            }
        }
        return username
    }

    companion object {
        private val log = LoggerFactory.getLogger(JwtUsernameExtractor::class.java)
    }

}

private const val AUTHORIZATION_TYPE = "Bearer "
