package net.plshark.users.auth.throttle

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Filter to limit the number of requests coming from an IP address
 */
class IpThrottlingFilter(
    private val maxRequests: Int = 100,
    timeFrame: Long = 60,
    timeFrameUnit: TimeUnit = TimeUnit.SECONDS
) : WebFilter {

    private val cache: LoadingCache<String, AtomicInteger> = Caffeine.newBuilder()
        .expireAfterWrite(timeFrame, timeFrameUnit)
        .build { AtomicInteger(0) }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val httpRequest = exchange.request
        val clientIp = getClientIp(httpRequest)
        return if (incrementAttempts(clientIp)) {
            log.info("Request from {} blocked", clientIp)
            Mono.fromRunnable { exchange.response.statusCode = HttpStatus.TOO_MANY_REQUESTS }
        } else {
            chain.filter(exchange)
        }
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
     * Increment the request count from an IP address
     * @param ip the IP address
     * @return if the IP address has made too many requests in the current time period
     */
    private fun incrementAttempts(ip: String): Boolean {
        val requests = cache[ip]!!.incrementAndGet()
        return requests > maxRequests
    }

    companion object {
        private val log = LoggerFactory.getLogger(IpThrottlingFilter::class.java)
    }
}
