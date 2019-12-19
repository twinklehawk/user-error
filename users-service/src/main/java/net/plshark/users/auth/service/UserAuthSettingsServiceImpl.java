package net.plshark.users.auth.service;

import java.util.Objects;
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

    public UserAuthSettingsServiceImpl(UserAuthSettingsRepository settingsRepo) {
        this.settingsRepo = Objects.requireNonNull(settingsRepo);
        this.defaultAuthSettings = UserAuthSettings.builder()
                .build();
    }

    @Override
    public Mono<UserAuthSettings> findByUsername(String username) {
        return settingsRepo.findByUsername(username)
                .defaultIfEmpty(defaultAuthSettings);
    }
}
