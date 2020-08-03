package net.plshark.users.auth.jwt

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * Prompts a user for HTTP Bearer authentication.
 */
class HttpBearerServerAuthenticationEntryPoint(realm: String = DEFAULT_REALM) :
    ServerAuthenticationEntryPoint {

    private val headerValue: String = String.format(WWW_AUTHENTICATE_FORMAT, realm)

    override fun commence(exchange: ServerWebExchange, e: AuthenticationException): Mono<Void> {
        return Mono.fromRunnable {
            val response = exchange.response
            response.statusCode = HttpStatus.UNAUTHORIZED
            response.headers[WWW_AUTHENTICATE] = headerValue
        }
    }
}

private const val WWW_AUTHENTICATE = "WWW-Authenticate"
private const val DEFAULT_REALM = "Realm"
private const val WWW_AUTHENTICATE_FORMAT = "Bearer realm=\"%s\""
