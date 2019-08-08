package net.plshark.users.repo;

import net.plshark.users.model.Group;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupsRepository {

    Mono<Group> getForId(long id);

    Mono<Group> getForName(String name);

    Flux<Group> getRoles(int maxResults, long offset);

    Mono<Group> insert(Group group);

    Mono<Void> delete(long groupId);
}
