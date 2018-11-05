package net.plshark.auth.jwt;

import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Prompts a user for HTTP Bearer authentication.
 */
public class HttpBearerServerAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    private static final String DEFAULT_REALM = "Realm";
    private static final String WWW_AUTHENTICATE_FORMAT = "Bearer realm=\"%s\"";

    private final String headerValue;

    /**
     * Create a new instance using the default authentication realm
     */
    public HttpBearerServerAuthenticationEntryPoint() {
        this(DEFAULT_REALM);
    }

    /**
     * Create a new instance
     * @param realm the authentication realm
     */
    public HttpBearerServerAuthenticationEntryPoint(String realm) {
        headerValue = createHeaderValue(realm);
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        return Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().set(WWW_AUTHENTICATE, this.headerValue);
        });
    }

    private static String createHeaderValue(String realm) {
        Objects.requireNonNull(realm, "realm cannot be null");
        return String.format(WWW_AUTHENTICATE_FORMAT, realm);
    }
}
