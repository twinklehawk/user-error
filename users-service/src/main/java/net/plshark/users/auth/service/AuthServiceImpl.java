package net.plshark.users.auth.service;

import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import net.plshark.users.auth.model.AccountCredentials;
import net.plshark.users.auth.model.AuthToken;
import net.plshark.users.auth.model.AuthenticatedUser;
import net.plshark.users.auth.model.UserAuthSettings;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Default AuthService server side implementation
 */
@Component
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Nonnull private final PasswordEncoder passwordEncoder;
    @Nonnull private final ReactiveUserDetailsService userDetailsService;
    @Nonnull private final TokenVerifier tokenVerifier;
    @Nonnull private final TokenBuilder tokenBuilder;
    @Nonnull private final UserAuthSettingsService userAuthSettingsService;

    @Override
    public Mono<AuthToken> authenticate(AccountCredentials credentials) {
        String username = credentials.getUsername();
        return userDetailsService.findByUsername(username)
                .publishOn(Schedulers.parallel())
                .filter(user -> this.passwordEncoder.matches(credentials.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.error(() -> new BadCredentialsException("Invalid Credentials")))
                .flatMap(this::buildAuthToken);
    }

    @Override
    public Mono<AuthToken> refresh(String refreshToken) {
        return Mono.just(refreshToken)
                .publishOn(Schedulers.parallel())
                .map(tokenVerifier::verifyRefreshToken)
                .flatMap(userDetailsService::findByUsername)
                .switchIfEmpty(Mono.error(() -> new BadCredentialsException("Invalid Credentials")))
                // TODO run any checks to see if user is allowed to refresh
                .flatMap(this::buildAuthToken);
    }

    @Override
    public Mono<AuthenticatedUser> validateToken(String accessToken) {
        return Mono.just(accessToken)
                .publishOn(Schedulers.parallel())
                .map(tokenVerifier::verifyToken);
    }

    private Mono<AuthToken> buildAuthToken(UserDetails user) {
        return userAuthSettingsService.findByUsername(user.getUsername())
                .map(settings -> buildAuthToken(user, settings));
    }

    private AuthToken buildAuthToken(UserDetails user, UserAuthSettings settings) {
        long tokenExpiration = Optional.ofNullable(settings.getAuthTokenExpiration())
                .orElse(userAuthSettingsService.getDefaultTokenExpiration());
        String[] authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
        AuthToken.AuthTokenBuilder builder = AuthToken.builder()
                .expiresIn(tokenExpiration / 1000)
                .accessToken(tokenBuilder.buildAccessToken(user.getUsername(), tokenExpiration, authorities));

        if (settings.isRefreshTokenEnabled()) {
            long refreshExpiration = Optional.ofNullable(settings.getRefreshTokenExpiration())
                    .orElse(userAuthSettingsService.getDefaultTokenExpiration());
            builder.refreshToken(tokenBuilder.buildRefreshToken(user.getUsername(), refreshExpiration));
        }

        return builder.build();
    }
}
