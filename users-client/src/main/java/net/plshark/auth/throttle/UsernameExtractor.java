package net.plshark.auth.throttle;

import java.util.Optional;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * Extracts usernames from requests
 */
public interface UsernameExtractor {

    /**
     * Extract the username from a request
     * @param request the request
     * @return the username or empty if not found
     */
    Optional<String> extractUsername(ServerHttpRequest request);
}
