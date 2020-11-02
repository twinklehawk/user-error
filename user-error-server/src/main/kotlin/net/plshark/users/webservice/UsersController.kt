package net.plshark.users.webservice

import net.plshark.errors.BadRequestException
import net.plshark.errors.DuplicateException
import net.plshark.errors.ObjectNotFoundException
import net.plshark.users.model.Group
import net.plshark.users.model.PasswordChangeRequest
import net.plshark.users.model.Role
import net.plshark.users.model.User
import net.plshark.users.model.UserCreate
import net.plshark.users.repo.UserGroupsRepository
import net.plshark.users.repo.UserRolesRepository
import net.plshark.users.repo.UsersRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import javax.validation.constraints.Min

/**
 * Controller providing web service methods for users
 */
@RestController
@RequestMapping("/users")
class UsersController(
    private val userRepo: UsersRepository,
    private val userRolesRepo: UserRolesRepository,
    private val userGroupsRepo: UserGroupsRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUsers(
        @RequestParam(value = "max-results", defaultValue = "50") maxResults: @Min(1) Int,
        @RequestParam(value = "offset", defaultValue = "0") offset: @Min(0) Long
    ): Flux<User> {
        return userRepo.getAll(maxResults, offset)
    }

    @GetMapping(path = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(@PathVariable("id") id: Long): Mono<User> {
        return userRepo.findById(id)
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No user found for id") })
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody user: UserCreate): Mono<User> {
        if (user.password.isEmpty()) throw BadRequestException("Password cannot be empty")
        return Mono.just(user)
            .subscribeOn(Schedulers.parallel())
            .map { it.copy(password = passwordEncoder.encode(it.password)) }
            .flatMap { userRepo.insert(it) }
            .onErrorMap(DataIntegrityViolationException::class.java) { e: DataIntegrityViolationException? ->
                DuplicateException("A user with username ${user.username} already exists", e)
            }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): Mono<Void> {
        return userRepo.deleteById(id)
    }

    @PostMapping(path = ["/{id}/password"])
    fun changePassword(
        @PathVariable("id") id: Long,
        @RequestBody request: PasswordChangeRequest
    ): Mono<Void> {
        if (request.newPassword.isEmpty()) throw BadRequestException("New password cannot be empty")
        val newPasswordEncoded = passwordEncoder.encode(request.newPassword)
        val currentPasswordEncoded = passwordEncoder.encode(request.currentPassword)
        return findRequiredById(id)
            .flatMap { user: User ->
                userRepo.updatePassword(user.id, currentPasswordEncoded, newPasswordEncoded)
                    .onErrorResume(EmptyResultDataAccessException::class.java) { e: EmptyResultDataAccessException? ->
                        Mono.error(BadRequestException("Incorrect current password", e))
                    }
            }
    }

    @GetMapping(path = ["/{id}/roles"])
    fun getUserRoles(@PathVariable("id") id: Long): Flux<Role> {
        return userRolesRepo.findRolesByUserId(id)
    }

    @PutMapping(path = ["/{id}/roles"])
    @Transactional
    fun updateUserRoles(@PathVariable("id") id: Long, @RequestBody updatedRoles: Set<Role>): Flux<Role> {
        return findRequiredById(id)
            .thenMany(userRolesRepo.findRolesByUserId(id))
            .collectList()
            .map { existingRoles ->
                Flux.merge(
                    Flux.fromIterable(existingRoles.minus(updatedRoles))
                        .flatMap { userRolesRepo.deleteById(id, it.id) },
                    Flux.fromIterable(updatedRoles.minus(existingRoles))
                        .flatMap { userRolesRepo.insert(id, it.id) }
                )
            }.thenMany(Flux.fromIterable(updatedRoles))
    }

    @GetMapping(path = ["/{id}/groups"])
    fun getUserGroups(@PathVariable("id") id: Long): Flux<Group> {
        return userGroupsRepo.findGroupsByUserId(id)
    }

    @PutMapping(path = ["/{id}/groups"])
    @Transactional
    fun updateUserGroups(@PathVariable("id") id: Long, @RequestBody updatedGroups: Set<Group>): Flux<Group> {
        return findRequiredById(id)
            .thenMany(userGroupsRepo.findGroupsByUserId(id))
            .collectList()
            .map { existingGroups ->
                Flux.merge(
                    Flux.fromIterable(existingGroups.minus(updatedGroups))
                        .flatMap { userGroupsRepo.deleteById(id, it.id) },
                    Flux.fromIterable(updatedGroups.minus(existingGroups))
                        .flatMap { userGroupsRepo.insert(id, it.id) }
                )
            }.thenMany(Flux.fromIterable(updatedGroups))
    }

    private fun findRequiredById(id: Long): Mono<User> {
        return findById(id)
            .switchIfEmpty(Mono.error { ObjectNotFoundException("No user found for $id") })
    }
}
