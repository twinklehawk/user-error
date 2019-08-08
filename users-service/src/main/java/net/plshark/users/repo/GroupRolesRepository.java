package net.plshark.users.repo;

import net.plshark.users.model.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupRolesRepository {

    Flux<Role> getRolesForGroup(long groupId);

    Mono<Void> insert(long groupId, long roleId);

    Mono<Void> delete(long groupId, long roleId);

    Mono<Void> deleteForGroup(long groupId);

    Mono<Void> deleteForRole(long roleId);
}
