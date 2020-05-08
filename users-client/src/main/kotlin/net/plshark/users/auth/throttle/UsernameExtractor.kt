package net.plshark.users.auth.throttle

import org.springframework.http.server.reactive.ServerHttpRequest
import java.util.*

/**
 * Extracts usernames from requests
 */
interface UsernameExtractor {
    /**
     * Extract the username from a request
     * @param request the request
     * @return the username or empty if not found
     */
    fun extractUsername(request: ServerHttpRequest): Optional<String>
}