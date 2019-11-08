package net.plshark.users.service;

import java.util.Objects;
import net.plshark.BadRequestException;
import net.plshark.ObjectNotFoundException;
import net.plshark.users.model.User;
import net.plshark.users.repo.UserGroupsRepository;
import net.plshark.users.repo.UserRolesRepository;
import net.plshark.users.repo.UsersRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * UserManagementService implementation
 */
@Component
public class UsersServiceImpl implements UsersService {

    private final UsersRepository userRepo;
    private final UserRolesRepository userRolesRepo;
    private final UserGroupsRepository userGroupsRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new instance
     * @param userRepository the repository for accessing users
     * @param userRolesRepo the repository for accessing user roles
     * @param passwordEncoder the encoder to use to encode passwords
     */
    public UsersServiceImpl(UsersRepository userRepository, UserRolesRepository userRolesRepo,
                            UserGroupsRepository userGroupsRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = Objects.requireNonNull(userRepository, "userRepository cannot be null");
        this.userRolesRepo = Objects.requireNonNull(userRolesRepo, "userRolesRepo cannot be null");
        this.userGroupsRepo = Objects.requireNonNull(userGroupsRepo, "userGroupsRepo cannot be null");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "passwordEncoder cannot be null");
    }

    @Override
    public Mono<User> getUserByUsername(String username) {
        return userRepo.getForUsername(username);
    }

    @Override
    public Flux<User> getUsers(int maxResults, long offset) {
        return userRepo.getAll(maxResults, offset);
    }

    @Override
    public Mono<User> insertUser(User user) {
        return Mono.just(user)
                .flatMap(u -> u.getPassword() != null ? Mono.just(u) : Mono.error(() -> new BadRequestException("password cannot be empty")))
                .map(u -> u.toBuilder().password(passwordEncoder.encode(u.getPassword())).build())
                .flatMap(userRepo::insert);
    }

    @Override
    public Mono<Void> deleteUser(long userId) {
        return userRolesRepo.deleteUserRolesForUser(userId)
                .then(userGroupsRepo.deleteUserGroupsForUser(userId))
                .then(userRepo.delete(userId));
    }

    @Override
    public Mono<Void> deleteUser(String username) {
        //noinspection ConstantConditions
        return getUserByUsername(username)
                .flatMap(user -> deleteUser(user.getId()));
    }

    @Override
    public Mono<Void> grantRoleToUser(long userId, long roleId) {
        // TODO what status code when user or role does not exist?
        return userRolesRepo.insertUserRole(userId, roleId);
    }

    @Override
    public Mono<Void> removeRoleFromUser(long userId, long roleId) {
        return userRolesRepo.deleteUserRole(userId, roleId);
    }

    @Override
    public Mono<Void> addUserToGroup(long userId, long groupId) {
        return userGroupsRepo.insert(userId, groupId);
    }

    @Override
    public Mono<Void> removeUserFromGroup(long userId, long groupId) {
        return userGroupsRepo.delete(userId, groupId);
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
}
