package net.plshark.users.auth.throttle;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

/**
 * Filter to limit the number of requests coming from an IP address
 */
public class IpThrottlingFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(IpThrottlingFilter.class);

    private final int maxRequests;
    private final LoadingCache<String, AtomicInteger> cache;

    /**
     * Create a new instance allowing 100 requests per minute
     */
    public IpThrottlingFilter() {
        this(100, 60, TimeUnit.SECONDS);
    }

    /**
     * Create a new instance
     * @param maxRequests the maximum requests in the time frame before requests will be blocked
     * @param timeFrame the amount of time before resetting the request count
     * @param timeFrameUnit the units for {@code timeFrame}
     */
    public IpThrottlingFilter(int maxRequests, long timeFrame, TimeUnit timeFrameUnit) {
        this.maxRequests = maxRequests;
        cache = Caffeine.newBuilder().expireAfterWrite(timeFrame, timeFrameUnit)
                .build(key -> new AtomicInteger(0));
    }

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest httpRequest = exchange.getRequest();
        String clientIp = getClientIp(httpRequest);

        if (incrementAttempts(clientIp)) {
            log.info("Request from {} blocked", clientIp);
            return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS));
        } else {
            return chain.filter(exchange);
        }
    }

    /**
     * Get the IP address that sent a request
     * @param request the request
     * @return the IP address
     */
    private String getClientIp(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst("X-Forwarded-For"))
                // get the first entry if it was forwarded multiple times
                .map(header -> header.split(",")[0])
                .orElse(Optional.ofNullable(request.getRemoteAddress())
                        .map(InetSocketAddress::getHostString)
                        .orElse(""));
    }

    /**
     * Increment the request count from an IP address
     * @param ip the IP address
     * @return if the IP address has made too many requests in the current time period
     */
    private boolean incrementAttempts(String ip) {
        @SuppressWarnings("ConstantConditions")
        int requests = cache.get(ip).incrementAndGet();
        return requests > maxRequests;
    }
}
