package net.plshark.users.auth.service;

import java.util.Objects;
import net.plshark.users.auth.AuthProperties;
import net.plshark.users.auth.model.UserAuthSettings;
import net.plshark.users.auth.repo.UserAuthSettingsRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Implementation of {@link UserAuthSettingsService}
 */
@Component
public class UserAuthSettingsServiceImpl implements UserAuthSettingsService {

    private final UserAuthSettingsRepository settingsRepo;
    private final UserAuthSettings defaultAuthSettings;

    public UserAuthSettingsServiceImpl(UserAuthSettingsRepository settingsRepo, AuthProperties authProperties) {
        this.settingsRepo = Objects.requireNonNull(settingsRepo);
        this.defaultAuthSettings = UserAuthSettings.builder()
                .authTokenExpiration(authProperties.getTokenExpiration())
                .refreshTokenExpiration(authProperties.getTokenExpiration())
                .build();
    }

    @Override
    public Mono<UserAuthSettings> findByUsername(String username) {
        return settingsRepo.findByUsername(username)
                .defaultIfEmpty(defaultAuthSettings);
    }

    @Override
    public long getDefaultTokenExpiration() {
        return Objects.requireNonNull(defaultAuthSettings.getAuthTokenExpiration());
    }
}
