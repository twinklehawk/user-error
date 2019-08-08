package net.plshark.users.repo;

import net.plshark.users.model.Group;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserGroupsRepository {

    Flux<Group> getGroupsForUser(long userId);

    Mono<Void> insert(long userId, long groupId);

    Mono<Void> delete(long userId, long groupId);

    Mono<Void> deleteUserGroupsForUser(long userId);

    Mono<Void> deleteUserGroupsForGroup(long groupId);
}
