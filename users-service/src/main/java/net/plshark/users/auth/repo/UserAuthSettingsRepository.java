package net.plshark.users.auth.repo;

import net.plshark.users.auth.model.UserAuthSettings;
import reactor.core.publisher.Mono;

public interface UserAuthSettingsRepository {

    Mono<UserAuthSettings> findByUserId(long userId);

    Mono<UserAuthSettings> insert(UserAuthSettings userAuthSettings);
}
