package net.plshark.users.auth.throttle

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.net.InetSocketAddress
import java.nio.file.AccessDeniedException
import java.util.*

/**
 * Filter that blocks requests from an IP address or for a user if too many requests have been made
 * in a time frame
 */
class LoginAttemptThrottlingFilter(
    private val service: LoginAttemptService,
    private val usernameExtractor: UsernameExtractor
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val httpRequest = exchange.request
        var blocked = false
        val clientIp = getClientIp(httpRequest)
        val username = getUsername(httpRequest)
        if (service.isIpBlocked(clientIp)) {
            blocked = true
            log.debug("blocked request from {}", clientIp)
        } else if (service.isUsernameBlocked(username)) {
            blocked = true
            log.debug("blocked request for username {}", username)
        }
        return if (blocked) Mono.fromRunnable { exchange.response.statusCode = HttpStatus.TOO_MANY_REQUESTS }
        else chain.filter(exchange)
            .doOnError(AccessDeniedException::class.java) { service.onLoginFailed(username, clientIp) }
            .then(Mono.fromRunnable {
                val status = exchange.response.statusCode
                if (HttpStatus.UNAUTHORIZED == status || HttpStatus.FORBIDDEN == status)
                    service.onLoginFailed(username, clientIp)
            })
    }

    /**
     * Get the IP address that sent a request
     * @param request the request
     * @return the IP address
     */
    private fun getClientIp(request: ServerHttpRequest): String {
        return Optional.ofNullable(request.headers.getFirst("X-Forwarded-For"))
            // get the first entry if it was forwarded multiple times
            .map { header: String -> header.split(",")[0] }
            .orElse(
                Optional.ofNullable(request.remoteAddress)
                    .map { obj: InetSocketAddress -> obj.hostString }
                    .orElse("")
            )
    }

    /**
     * Get the username for the authentication in a request
     * @param request the request
     * @return the username
     */
    private fun getUsername(request: ServerHttpRequest): String {
        return usernameExtractor.extractUsername(request)
            .orElse("")
    }

    companion object {
        private val log = LoggerFactory.getLogger(LoginAttemptThrottlingFilter::class.java)
    }
}
