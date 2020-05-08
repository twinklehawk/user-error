package net.plshark.users.auth.throttle.impl

import net.plshark.users.auth.throttle.UsernameExtractor
import org.slf4j.LoggerFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Username extractor for requests using basic authentication
 */
class BasicAuthenticationUsernameExtractor : UsernameExtractor {

    override fun extractUsername(request: ServerHttpRequest): Optional<String> {
        return Optional.ofNullable(request.headers.getFirst("Authorization"))
            .flatMap { header: String -> this.extractUsername(header) }
    }

    /**
     * Extract the username from the Authorization header value
     * @param header the header value
     * @return the username or empty if not found
     */
    private fun extractUsername(header: String): Optional<String> {
        var username: Optional<String> = Optional.empty()
        if (header.startsWith("Basic ")) {
            try {
                val base64Auth = header.substring(6).toByteArray(StandardCharsets.UTF_8)
                val decoded = Base64.getDecoder().decode(base64Auth)
                val auth = String(decoded, StandardCharsets.UTF_8)
                val colonIndex = auth.indexOf(':')
                if (colonIndex != -1) username = Optional.of(auth.substring(0, colonIndex))
            } catch (e: IllegalArgumentException) {
                log.debug("invalid base64 encoding in Authorization header", e)
                username = Optional.empty()
            }
        }
        return username
    }

    companion object {
        private val log = LoggerFactory.getLogger(BasicAuthenticationUsernameExtractor::class.java)
    }
}