package net.plshark.auth.throttle;

import java.nio.file.AccessDeniedException;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * Filter that blocks requests from an IP address or for a user if too many requests have been made
 * in a time frame
 */
public class LoginAttemptThrottlingFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptThrottlingFilter.class);

    private final LoginAttemptService service;
    private final UsernameExtractor usernameExtractor;

    /**
     * Create a new instance
     * @param service the service to track what IPs and usernames are blocked
     * @param usernameExtractor the extractor to retrieve the username from a request
     */
    public LoginAttemptThrottlingFilter(LoginAttemptService service, UsernameExtractor usernameExtractor) {
        this.service = Objects.requireNonNull(service);
        this.usernameExtractor = Objects.requireNonNull(usernameExtractor);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest httpRequest = exchange.getRequest();
        boolean blocked = false;

        String clientIp = getClientIp(httpRequest);
        String username = getUsername(httpRequest).orElse("");
        if (service.isIpBlocked(clientIp)) {
            blocked = true;
            log.debug("blocked request from {}", clientIp);
        } else if (service.isUsernameBlocked(username)) {
            blocked = true;
            log.debug("blocked request for username {}", username);
        }

        if (blocked)
            return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS));
        else
            return chain.filter(exchange)
                .doOnError(AccessDeniedException.class, error -> service.onLoginFailed(username, clientIp))
                .then(Mono.fromRunnable(() -> {
                    HttpStatus status = exchange.getResponse().getStatusCode();
                    if (HttpStatus.UNAUTHORIZED.equals(status) || HttpStatus.FORBIDDEN.equals(status))
                        service.onLoginFailed(username, clientIp);
                }));
    }

    /**
     * Get the IP address that sent a request
     * @param request the request
     * @return the IP address
     */
    private String getClientIp(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst("X-Forwarded-For"))
            .map(header -> header.split(",")[0])
            .orElse(Optional.ofNullable(request.getRemoteAddress())
                .map(inetAddr -> inetAddr.getHostString())
                .orElse(""));
    }

    /**
     * Get the username for the authentication in a request
     * @param request the request
     * @return the username or empty if not found
     */
    private Optional<String> getUsername(ServerHttpRequest request) {
        return usernameExtractor.extractUsername(request);
    }
}
