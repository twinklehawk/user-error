package net.plshark.usererror.authentication.token

import net.plshark.usererror.authorization.AuthorizationService
import org.springframework.http.MediaType
import org.springframework.security.web.server.DelegatingServerAuthenticationEntryPoint
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.MediaTypeServerWebExchangeMatcher
import org.springframework.web.server.WebFilter

/**
 * Builds the filter and entry point necessary to use HTTP bearer authentication
 */
class HttpBearerBuilder(
    private val authenticationManager: JwtReactiveAuthenticationManager,
    private val entryPoint: HttpBearerServerAuthenticationEntryPoint = HttpBearerServerAuthenticationEntryPoint()
) {

    /**
     * Create a new instance
     * @param authService the authorization service
     */
    constructor(authService: AuthorizationService) : this(JwtReactiveAuthenticationManager(authService))

    /**
     * @return the bearer authorization web filter
     */
    fun buildFilter(): WebFilter {
        val authenticationFilter = AuthenticationWebFilter(authenticationManager)
        authenticationFilter.setAuthenticationFailureHandler(ServerAuthenticationEntryPointFailureHandler(entryPoint))
        authenticationFilter.setServerAuthenticationConverter(ServerHttpJwtAuthenticationConverter())
        authenticationFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        return authenticationFilter
    }

    /**
     * @return the bearer entry point
     */
    // TODO remove?
    fun buildEntryPoint(): DelegatingServerAuthenticationEntryPoint.DelegateEntry {
        val restMatcher = MediaTypeServerWebExchangeMatcher(
            MediaType.APPLICATION_ATOM_XML,
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_OCTET_STREAM,
            MediaType.APPLICATION_XML,
            MediaType.MULTIPART_FORM_DATA,
            MediaType.TEXT_XML
        )
        restMatcher.setIgnoredMediaTypes(setOf(MediaType.ALL))
        return DelegatingServerAuthenticationEntryPoint.DelegateEntry(restMatcher, entryPoint)
    }
}
