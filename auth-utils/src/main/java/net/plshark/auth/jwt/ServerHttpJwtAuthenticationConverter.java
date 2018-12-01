package net.plshark.auth.jwt;

import java.util.Optional;
import java.util.function.Function;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Converts JWT authentication in a request to an {@link Authentication}
 */
public class ServerHttpJwtAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>> {

    private static final String BEARER = "Bearer ";

    @Override
    public Mono<Authentication> apply(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        return Optional.ofNullable(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authorization -> authorization.startsWith(BEARER))
                .map(authorization -> authorization.substring(BEARER.length()))
                .map(token -> Mono.just((Authentication) JwtAuthenticationToken.builder().withToken(token).build()))
                .orElse(Mono.empty());
    }
}
