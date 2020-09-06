@file:Suppress("TooManyFunctions")

package net.plshark.users.service

import net.plshark.errors.BadRequestException
import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Group
import net.plshark.users.model.Role
import net.plshark.users.model.User
import net.plshark.users.model.UserCreate
import net.plshark.users.repo.UserGroupsRepository
import net.plshark.users.repo.UserRolesRepository
import net.plshark.users.repo.UsersRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
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

    override fun getUserRoles(userId: Long): Flux<Role> {
        return findRequiredById(userId)
            .thenMany(userRolesRepo.findRolesByUserId(userId))
    }

    @Transactional
    override fun updateUserRoles(userId: Long, updatedRoles: Set<Role>): Flux<Role> {
        return findRequiredById(userId)
            .thenMany(userRolesRepo.findRolesByUserId(userId))
            .collectList()
            .map { existingRoles ->
                Flux.merge(
                    Flux.fromIterable(existingRoles.minus(updatedRoles))
                        .flatMap { userRolesRepo.deleteById(userId, it.id) },
                    Flux.fromIterable(updatedRoles.minus(existingRoles))
                        .flatMap { userRolesRepo.insert(userId, it.id) }
                )
            }.thenMany(Flux.fromIterable(updatedRoles))
    }

    override fun getUserGroups(userId: Long): Flux<Group> {
        return findRequiredById(userId)
            .thenMany(userGroupsRepo.findGroupsByUserId(userId))
    }

    @Transactional
    override fun updateUserGroups(userId: Long, updatedGroups: Set<Group>): Flux<Group> {
        return findRequiredById(userId)
            .thenMany(userGroupsRepo.findGroupsByUserId(userId))
            .collectList()
            .map { existingGroups ->
                Flux.merge(
                    Flux.fromIterable(existingGroups.minus(updatedGroups))
                        .flatMap { userGroupsRepo.deleteById(userId, it.id) },
                    Flux.fromIterable(updatedGroups.minus(existingGroups))
                        .flatMap { userGroupsRepo.insert(userId, it.id) }
                )
            }.thenMany(Flux.fromIterable(updatedGroups))
    }

    override fun updateUserPassword(id: Long, currentPassword: String, newPassword: String): Mono<Void> {
        require(newPassword.isNotEmpty()) { "New password cannot be empty" }
        val newPasswordEncoded = passwordEncoder.encode(newPassword)
        val currentPasswordEncoded = passwordEncoder.encode(currentPassword)
        return findRequiredById(id)
            .flatMap { user: User ->
                userRepo.updatePassword(user.id, currentPasswordEncoded, newPasswordEncoded)
                    .onErrorResume(EmptyResultDataAccessException::class.java) { e: EmptyResultDataAccessException? ->
                        Mono.error(BadRequestException("Incorrect current password", e))
                    }
            }
    }
}
