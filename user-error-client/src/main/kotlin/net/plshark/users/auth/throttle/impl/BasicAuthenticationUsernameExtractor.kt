package net.plshark.users.auth.throttle.impl

import net.plshark.users.auth.throttle.UsernameExtractor
import org.slf4j.LoggerFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * Username extractor for requests using basic authentication
 */
class BasicAuthenticationUsernameExtractor : UsernameExtractor {

    override fun extractUsername(request: ServerHttpRequest): String? {
        return request.headers.getFirst("Authorization")?.let { extractUsername(it) }
    }

    /**
     * Extract the username from the Authorization header value
     * @param header the header value
     * @return the username or empty if not found
     */
    private fun extractUsername(header: String): String? {
        var username: String? = null
        val prefix = "Basic "
        if (header.startsWith(prefix)) {
            try {
                val base64Auth = header.substring(prefix.length).toByteArray(StandardCharsets.UTF_8)
                val decoded = Base64.getDecoder().decode(base64Auth)
                val auth = String(decoded, StandardCharsets.UTF_8)
                val colonIndex = auth.indexOf(':')
                if (colonIndex != -1) username = auth.substring(0, colonIndex)
            } catch (e: IllegalArgumentException) {
                log.debug("invalid base64 encoding in Authorization header", e)
            }
        }
        return username
    }

    companion object {
        private val log = LoggerFactory.getLogger(BasicAuthenticationUsernameExtractor::class.java)
    }
}
