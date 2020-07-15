package net.plshark.users.auth.throttle

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.nio.file.AccessDeniedException

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
        val result: Mono<Void>
        val clientIp = getClientIp(httpRequest)
        val username = getUsername(httpRequest)

        if (service.isIpBlocked(clientIp) || service.isUsernameBlocked(username)) {
            log.debug("blocked request from {} for username {}", clientIp, username)
            result = Mono.fromRunnable { exchange.response.statusCode = HttpStatus.TOO_MANY_REQUESTS }
        } else {
            result = chain.filter(exchange)
                .doOnError(AccessDeniedException::class.java) { service.onLoginFailed(username, clientIp) }
                .then(Mono.fromRunnable {
                    val status = exchange.response.statusCode
                    if (isLoginFailedStatus(status)) service.onLoginFailed(username, clientIp)
                })
        }

        return result
    }

    /**
     * @return if the status indicates a login failed
     */
    private fun isLoginFailedStatus(status: HttpStatus?): Boolean {
        return HttpStatus.UNAUTHORIZED == status || HttpStatus.FORBIDDEN == status
    }

    /**
     * Get the IP address that sent a request
     * @param request the request
     * @return the IP address
     */
    private fun getClientIp(request: ServerHttpRequest): String {
        // get the first entry if it was forwarded multiple times
        return request.headers.getFirst("X-Forwarded-For")?.split(",")?.get(0)
            ?: request.remoteAddress?.hostString ?: ""
    }

    /**
     * Get the username for the authentication in a request
     * @param request the request
     * @return the username
     */
    private fun getUsername(request: ServerHttpRequest): String {
        return usernameExtractor.extractUsername(request) ?: ""
    }

    companion object {
        private val log = LoggerFactory.getLogger(LoginAttemptThrottlingFilter::class.java)
    }
}
