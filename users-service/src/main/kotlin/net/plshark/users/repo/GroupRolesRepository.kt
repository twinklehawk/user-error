package net.plshark.users.repo

import net.plshark.users.model.Role
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

// TODO docs
interface GroupRolesRepository {
    fun getRolesForGroup(groupId: Long): Flux<Role>
    fun insert(groupId: Long, roleId: Long): Mono<Void>
    fun delete(groupId: Long, roleId: Long): Mono<Void>
    fun deleteForGroup(groupId: Long): Mono<Void>
    fun deleteForRole(roleId: Long): Mono<Void>
}