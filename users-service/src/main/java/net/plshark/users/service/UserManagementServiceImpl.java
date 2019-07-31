package net.plshark.users.service;

import java.util.Objects;
import javax.inject.Named;
import javax.inject.Singleton;

import net.plshark.users.model.Role;
import net.plshark.users.model.User;
import net.plshark.users.model.UserInfo;
import net.plshark.users.repo.RolesRepository;
import net.plshark.users.repo.UserRolesRepository;
import net.plshark.users.repo.UsersRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.plshark.ObjectNotFoundException;
import reactor.core.publisher.Flux;
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
    public Mono<UserInfo> getUserByUsername(String username) {
        return userRepo.getForUsername(username).map(UserInfo::fromUser);
    }

    @Override
    public Flux<UserInfo> getUsers(int maxResults, long offset) {
        return userRepo.getAll(maxResults, offset).map(UserInfo::fromUser);
    }

    @Override
    public Mono<UserInfo> insertUser(User user) {
        return userRepo.insert(User.create(user.getUsername(), passwordEncoder.encode(user.getPassword())))
                .map(UserInfo::fromUser);
    }

    @Override
    public Mono<Void> deleteUser(long userId) {
        return userRolesRepo.deleteUserRolesForUser(userId)
            .then(userRepo.delete(userId));
    }

    @Override
    public Mono<Void> deleteUser(UserInfo user) {
        return deleteUser(user.getId());
    }

    @Override
    public Mono<Role> insertRole(Role role) {
        return roleRepo.insert(role);
    }

    @Override
    public Mono<Void> deleteRole(long roleId) {
        return userRolesRepo.deleteUserRolesForRole(roleId)
            .then(roleRepo.delete(roleId));
    }

    @Override
    public Mono<Void> grantRoleToUser(long userId, long roleId) {
        return userRolesRepo.insertUserRole(userId, roleId);
    }

    @Override
    public Mono<Void> grantRoleToUser(UserInfo user, Role role) {
        Objects.requireNonNull(role.getId(), "Role ID must be set");
        return grantRoleToUser(user.getId(), role.getId());
    }

    @Override
    public Mono<Void> removeRoleFromUser(long userId, long roleId) {
        return userRolesRepo.deleteUserRole(userId, roleId);
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

    @Override
    public Flux<Role> getRoles(int maxResults, long offset) {
        return roleRepo.getRoles(maxResults, offset);
    }
}
