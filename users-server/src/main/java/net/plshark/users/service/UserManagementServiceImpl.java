package net.plshark.users.service;

import java.util.Objects;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.plshark.ObjectNotFoundException;
import net.plshark.users.Role;
import net.plshark.users.User;
import net.plshark.users.repo.RolesRepository;
import net.plshark.users.repo.UserRolesRepository;
import net.plshark.users.repo.UsersRepository;
import net.plshark.users.service.UserManagementService;
import reactor.core.publisher.Mono;

/**
 * UserManagementService implementation
 */
@Named
@Singleton
public class UserManagementServiceImpl implements UserManagementService {

    private final UsersRepository userRepo;
    private final RolesRepository roleRepo;
    private final UserRolesRepository userRolesRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new instance
     * @param userRepository the repository for accessing users
     * @param roleRepository the repository for accessing roles
     * @param userRolesRepo the repository for accessing user roles
     * @param passwordEncoder the encoder to use to encode passwords
     */
    public UserManagementServiceImpl(UsersRepository userRepository, RolesRepository roleRepository,
            UserRolesRepository userRolesRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = Objects.requireNonNull(userRepository, "userRepository cannot be null");
        this.roleRepo = Objects.requireNonNull(roleRepository, "roleRepository cannot be null");
        this.userRolesRepo = Objects.requireNonNull(userRolesRepo, "userRolesRepo cannot be null");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "passwordEncoder cannot be null");
    }

    @Override
    public Mono<User> saveUser(User user) {
        if (user.getId().isPresent())
            throw new IllegalArgumentException("Updating a user is not supported");

        user.setPassword(passwordEncoder.encode(user.getPassword().get()));

        return userRepo.insert(user);
    }

    @Override
    public Mono<Void> deleteUser(long userId) {
        return userRolesRepo.deleteUserRolesForUser(userId)
            .then(userRepo.delete(userId));
    }

    @Override
    public Mono<Void> deleteUser(User user) {
        return deleteUser(user.getId().get());
    }

    @Override
    public Mono<Role> saveRole(Role role) {
        if (role.getId().isPresent())
            throw new IllegalArgumentException("Updating a role is not supported");

        return roleRepo.insert(role);
    }

    @Override
    public Mono<Void> deleteRole(long roleId) {
        return userRolesRepo.deleteUserRolesForRole(roleId)
            .then(roleRepo.delete(roleId));
    }

    @Override
    public Mono<Void> grantRoleToUser(long userId, long roleId) {
        // TODO this kinda sucks
        return userRepo.getForId(userId)
            .switchIfEmpty(Mono.error(new ObjectNotFoundException("User not found")))
            .then(roleRepo.getForId(roleId))
            .switchIfEmpty(Mono.error(new ObjectNotFoundException("Role not found")))
            // if something else is deleting users/roles at the same time as this runs, the row can be inserted
            // with no matching user or role, but it doesn't really matter
            .flatMap(role -> userRolesRepo.insertUserRole(userId, roleId));
    }

    @Override
    public Mono<Void> grantRoleToUser(User user, Role role) {
        return grantRoleToUser(user.getId().get(), role.getId().get());
    }

    @Override
    public Mono<Void> removeRoleFromUser(long userId, long roleId) {
        // TODO this kinda sucks too
        return userRepo.getForId(userId)
            .switchIfEmpty(Mono.error(new ObjectNotFoundException("User not found")))
            // if something else is deleting users at the same time as this runs, the row can be inserted
            // with no matching user, but it doesn't really matter
            .flatMap(user -> userRolesRepo.deleteUserRole(userId, roleId));
    }

    @Override
    public Mono<Void> updateUserPassword(long userId, String currentPassword, String newPassword) {
        Objects.requireNonNull(currentPassword, "currentPassword cannot be null");
        Objects.requireNonNull(newPassword, "newPassword cannot be null");

        String newPasswordEncoded = passwordEncoder.encode(newPassword);
        String currentPasswordEncoded = passwordEncoder.encode(currentPassword);

        return userRepo.updatePassword(userId, currentPasswordEncoded, newPasswordEncoded)
            .onErrorResume(EmptyResultDataAccessException.class, e -> Mono.error(new ObjectNotFoundException("User not found", e)));
    }

    @Override
    public Mono<Role> getRoleByName(String name) {
        return roleRepo.getForName(name);
    }
}
