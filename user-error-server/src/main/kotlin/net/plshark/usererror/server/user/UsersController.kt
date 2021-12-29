package net.plshark.usererror.server.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import net.plshark.usererror.error.BadRequestException
import net.plshark.usererror.error.NotFoundException
import net.plshark.usererror.role.Group
import net.plshark.usererror.role.Role
import net.plshark.usererror.server.role.UserGroupsRepository
import net.plshark.usererror.server.role.UserRolesRepository
import net.plshark.usererror.user.PasswordChangeRequest
import net.plshark.usererror.user.User
import net.plshark.usererror.user.UserCreate
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
    ): Flow<User> {
        return userRepo.getAll(maxResults, offset)
    }

    @GetMapping(path = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun findById(@PathVariable("id") id: Long): User {
        return userRepo.findById(id) ?: throw NotFoundException("No user found for id")
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun create(@RequestBody user: UserCreate): User {
        if (user.password.isEmpty()) throw BadRequestException("Password cannot be empty")
        val encoded = user.copy(password = passwordEncoder.encode(user.password))
        try {
            return userRepo.insert(encoded)
        } catch (e: DataIntegrityViolationException) {
            throw net.plshark.usererror.error.DuplicateException(
                "A user with username ${user.username} already exists",
                e
            )
        }
    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable("id") id: Long) {
        userRepo.deleteById(id)
    }

    @PostMapping(path = ["/{id}/password"])
    suspend fun changePassword(
        @PathVariable("id") id: Long,
        @RequestBody request: PasswordChangeRequest
    ) {
        if (request.newPassword.isEmpty()) throw BadRequestException("New password cannot be empty")
        val newPasswordEncoded = passwordEncoder.encode(request.newPassword)
        val currentPasswordEncoded = passwordEncoder.encode(request.currentPassword)
        // TODO necessary?
        val user = findById(id)
        try {
            userRepo.updatePassword(user.id, currentPasswordEncoded, newPasswordEncoded)
        } catch (e: EmptyResultDataAccessException) {
            throw BadRequestException("Incorrect current password", e)
        }
    }

    @GetMapping(path = ["/{id}/roles"])
    fun getUserRoles(@PathVariable("id") id: Long): Flow<Role> {
        return userRolesRepo.findRolesByUserId(id)
    }

    @PutMapping(path = ["/{id}/roles"])
    @Transactional
    suspend fun updateUserRoles(@PathVariable("id") id: Long, @RequestBody updatedRoles: Set<Role>): Flow<Role> {
        // TODO necessary?
        findById(id)
        val existingRoles = userRolesRepo.findRolesByUserId(id).toList()
        // TODO make parallel
        existingRoles.minus(updatedRoles).forEach { userRolesRepo.deleteById(id, it.id) }
        updatedRoles.minus(existingRoles).forEach { userRolesRepo.insert(id, it.id) }
        return flow {
            updatedRoles.forEach { emit(it) }
        }
    }

    @GetMapping(path = ["/{id}/groups"])
    fun getUserGroups(@PathVariable("id") id: Long): Flow<Group> {
        return userGroupsRepo.findGroupsByUserId(id)
    }

    @PutMapping(path = ["/{id}/groups"])
    @Transactional
    suspend fun updateUserGroups(@PathVariable("id") id: Long, @RequestBody updatedGroups: Set<Group>): Flow<Group> {
        // TODO necessary?
        findById(id)
        val existingGroups = userGroupsRepo.findGroupsByUserId(id).toList()
        // TODO make parallel
        existingGroups.minus(updatedGroups).forEach { userGroupsRepo.deleteById(id, it.id) }
        updatedGroups.minus(existingGroups).forEach { userGroupsRepo.insert(id, it.id) }
        return flow {
            updatedGroups.forEach { emit(it) }
        }
    }
}
