package net.plshark.usererror.authentication.token.spring

import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * Converts JWT authentication in a request to an [Authentication]
 */
class ServerHttpJwtAuthenticationConverter : ServerAuthenticationConverter {

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val request = exchange.request
        return Mono.justOrEmpty(request.headers.getFirst(HttpHeaders.AUTHORIZATION))
            .filter { authorization: String -> authorization.startsWith(BEARER) }
            .map { authorization: String -> authorization.substring(BEARER.length) }
            .flatMap { token: String ->
                Mono.just(
                    JwtAuthenticationToken(
                        token = token,
                        username = null,
                        authenticated = false,
                        authorities = setOf()
                    ) as Authentication
                )
            }
    }

    companion object {
        private const val BEARER = "Bearer "
    }
}
