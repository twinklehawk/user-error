package net.plshark.users.auth.throttle.impl

import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import net.plshark.users.auth.throttle.UsernameExtractor
import org.slf4j.LoggerFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import java.util.*

/**
 * Username extractor for requests using bearer authentication with JWT
 */
class JwtUsernameExtractor(private val verifier: JWTVerifier) : UsernameExtractor {

    override fun extractUsername(request: ServerHttpRequest): Optional<String> {
        return Optional.ofNullable(request.headers.getFirst("Authorization"))
            .flatMap { header: String -> this.extractUsername(header) }
    }

    /**
     * Extract the username from an authorization header value
     * @param header the header value, preferably a valid JWT
     * @return the username
     */
    private fun extractUsername(header: String): Optional<String> {
        return try {
            Optional.of(header)
                .filter { str: String -> str.startsWith(AUTHORIZATION_TYPE) }
                .map { str: String -> verifier.verify(str.substring(AUTHORIZATION_TYPE.length)) }
                .map { obj: DecodedJWT -> obj.subject }
        } catch (e: JWTVerificationException) {
            log.debug("Invalid JWT token", e)
            Optional.empty()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(JwtUsernameExtractor::class.java)
    }

}

private const val AUTHORIZATION_TYPE = "Bearer "
