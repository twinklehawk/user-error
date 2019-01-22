package net.plshark.auth.service;

import java.util.Objects;
import net.plshark.auth.model.AccountCredentials;
import net.plshark.auth.model.AuthToken;
import net.plshark.auth.model.AuthenticatedUser;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Default AuthService server side implementation
 */
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final ReactiveUserDetailsService userDetailsService;
    private final TokenVerifier tokenVerifier;
    private final TokenBuilder tokenBuilder;
    private final long defaultExpirationMs;

    /**
     * Create a new instance
     * @param passwordEncoder the encoder to use to encode passwords to validate against stored credentials
     * @param userDetailsService the service to use to look up user information
     * @param tokenVerifier the object to use to verify tokens
     * @param tokenBuilder the object to use to build new tokens
     * @param expirationMs the default number of milliseconds until a token expires
     */
    public AuthServiceImpl(PasswordEncoder passwordEncoder, ReactiveUserDetailsService userDetailsService,
                           TokenVerifier tokenVerifier, TokenBuilder tokenBuilder, long expirationMs) {
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
        this.tokenVerifier = Objects.requireNonNull(tokenVerifier);
        this.tokenBuilder = Objects.requireNonNull(tokenBuilder);
        this.defaultExpirationMs = expirationMs;
    }

    @Override
    public Mono<AuthToken> authenticate(AccountCredentials credentials) {
        String username = credentials.getUsername();
        return this.userDetailsService.findByUsername(username)
                .publishOn(Schedulers.parallel())
                .filter(user -> this.passwordEncoder.matches(credentials.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid Credentials"))))
                .map(this::buildAuthToken);
    }

    @Override
    public Mono<AuthToken> refresh(String refreshToken) {
        return Mono.just(refreshToken)
                .publishOn(Schedulers.parallel())
                .flatMap(tokenVerifier::verifyRefreshToken)
                .flatMap(userDetailsService::findByUsername)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid Credentials"))))
                // TODO run any checks to see if user is allowed to refresh
                .map(this::buildAuthToken);
    }

    @Override
    public Mono<AuthenticatedUser> validateToken(String accessToken) {
        return Mono.just(accessToken)
                .publishOn(Schedulers.parallel())
                .flatMap(tokenVerifier::verifyToken);
    }

    // TODO make expiration configurable
    // TODO refresh token should be optional
    private AuthToken buildAuthToken(UserDetails user) {
        long expirationMs = this.defaultExpirationMs;
        String accessToken = tokenBuilder.buildAccessToken(user.getUsername(), expirationMs,
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new));
        String refreshToken = tokenBuilder.buildRefreshToken(user.getUsername(), expirationMs);
        return new AuthToken(accessToken, AuthToken.DEFAULT_TOKEN_TYPE, expirationMs / 1000, refreshToken, null);
    }
}
