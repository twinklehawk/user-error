@file:Suppress("TooManyFunctions")
package net.plshark.users.service

import net.plshark.errors.BadRequestException
import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.User
import net.plshark.users.model.UserCreate
import net.plshark.users.repo.UserGroupsRepository
import net.plshark.users.repo.UserRolesRepository
import net.plshark.users.repo.UsersRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

/**
 * [UsersService] implementation
 */
@Component
class UsersServiceImpl(
    private val userRepo: UsersRepository,
    private val userRolesRepo: UserRolesRepository,
    private val userGroupsRepo: UserGroupsRepository,
    private val rolesService: RolesService,
    private val groupsService: GroupsService,
    private val passwordEncoder: PasswordEncoder
) : UsersService {

    override fun findById(id: Long): Mono<User> {
        return userRepo.findById(id)
    }

    private fun findRequiredById(id: Long): Mono<User> {
        return findById(id)
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No user found for $id") })
    }

    override fun findByUsername(username: String): Mono<User> {
        return userRepo.findByUsername(username)
    }

    override fun findRequiredByUsername(username: String): Mono<User> {
        return findByUsername(username)
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No user found for $username") })
    }

    override fun getUsers(maxResults: Int, offset: Long): Flux<User> {
        return userRepo.getAll(maxResults, offset)
    }

    override fun create(user: UserCreate): Mono<User> {
        require(StringUtils.hasLength(user.password)) { "Password cannot be empty" }
        return Mono.just(user)
            .subscribeOn(Schedulers.parallel())
            .map { it.copy(password = passwordEncoder.encode(it.password)) }
            .flatMap { userRepo.insert(it) }
            .onErrorMap(DataIntegrityViolationException::class.java) { e: DataIntegrityViolationException? ->
                DuplicateException("A user with username ${user.username} already exists", e)
            }
    }

    override fun deleteById(userId: Long): Mono<Void> {
        return userRepo.deleteById(userId)
    }

    // TODO applicationId necessary?

    override fun grantRoleToUser(id: Long, applicationId: Long, roleId: Long): Mono<Void> {
        // TODO need to look up user or role?
        return findRequiredById(id)
            .flatMap { user: User ->
                rolesService.findRequiredById(roleId)
                    .flatMap { role -> userRolesRepo.insert(user.id, role.id) }
            }
    }

    override fun removeRoleFromUser(id: Long, applicationId: Long, roleId: Long): Mono<Void> {
        // TODO need to look up user or role?
        return findRequiredById(id)
            .flatMap { user: User ->
                rolesService.findRequiredById(roleId)
                    .flatMap { role -> userRolesRepo.deleteById(user.id, role.id) }
            }
    }

    override fun grantGroupToUser(id: Long, groupId: Long): Mono<Void> {
        // TODO need to look up user or group?
        return findRequiredById(id)
            .flatMap { user: User ->
                groupsService.findRequiredById(groupId)
                    .flatMap { group -> userGroupsRepo.insert(user.id, group.id) }
            }
    }

    override fun removeGroupFromUser(id: Long, groupId: Long): Mono<Void> {
        // TODO need to look up user or group?
        return findRequiredById(id)
            .flatMap { user: User ->
                groupsService.findRequiredById(groupId)
                    .flatMap { group -> userGroupsRepo.deleteById(user.id, group.id) }
            }
    }

    override fun updateUserPassword(id: Long, currentPassword: String, newPassword: String): Mono<Void> {
        require(newPassword.isNotEmpty()) { "New password cannot be empty" }
        val newPasswordEncoded = passwordEncoder.encode(newPassword)
        val currentPasswordEncoded = passwordEncoder.encode(currentPassword)
        // TODO need to look up user by ID?
        return findRequiredById(id)
            .flatMap { user: User ->
                userRepo.updatePassword(user.id, currentPasswordEncoded, newPasswordEncoded)
                    .onErrorResume(EmptyResultDataAccessException::class.java) { e: EmptyResultDataAccessException? ->
                        Mono.error(BadRequestException("Incorrect current password", e))
                    }
            }
    }
}
