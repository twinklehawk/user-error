package net.plshark.auth.jwt;

import java.util.Collections;
import com.auth0.jwt.JWTVerifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.DelegatingServerAuthenticationEntryPoint;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.MediaTypeServerWebExchangeMatcher;
import org.springframework.web.server.WebFilter;

/**
 * Builds the filter and entry point necessary to use HTTP bearer authentication
 */
public class HttpBearerBuilder {

    private ReactiveAuthenticationManager authenticationManager;
    private ServerAuthenticationEntryPoint entryPoint = new HttpBearerServerAuthenticationEntryPoint();

    /**
     * Create a new instance
     * @param jwtVerifier the verifier to use to validate and decode JWTs
     */
    public HttpBearerBuilder(JWTVerifier jwtVerifier) {
        this.authenticationManager = new JwtReactiveAuthenticationManager(jwtVerifier);
    }

    /**
     * @return the bearer authorization web filter
     */
    public WebFilter buildFilter() {
        AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(entryPoint));
        authenticationFilter.setServerAuthenticationConverter(new ServerHttpJwtAuthenticationConverter());
        authenticationFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        return authenticationFilter;
    }

    /**
     * @return the bearer entry point
     */
    public DelegatingServerAuthenticationEntryPoint.DelegateEntry buildEntryPoint() {
        MediaTypeServerWebExchangeMatcher restMatcher = new MediaTypeServerWebExchangeMatcher(
                MediaType.APPLICATION_ATOM_XML,
                MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_XML,
                MediaType.MULTIPART_FORM_DATA, MediaType.TEXT_XML);
        restMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        return new DelegatingServerAuthenticationEntryPoint.DelegateEntry(restMatcher, entryPoint);
    }
}
