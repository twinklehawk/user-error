package net.plshark.users.service;

import java.util.Objects;
import net.plshark.errors.DuplicateException;
import net.plshark.errors.ObjectNotFoundException;
import net.plshark.users.model.User;
import net.plshark.users.repo.UserGroupsRepository;
import net.plshark.users.repo.UserRolesRepository;
import net.plshark.users.repo.UsersRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * UserManagementService implementation
 */
@Component
public class UsersServiceImpl implements UsersService {

    private final UsersRepository userRepo;
    private final UserRolesRepository userRolesRepo;
    private final UserGroupsRepository userGroupsRepo;
    private final RolesService rolesService;
    private final GroupsService groupsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new instance
     * @param userRepository the repository for accessing users
     * @param userRolesRepo the repository for accessing user roles
     * @param userGroupsRepo the repository for user groups
     * @param rolesService the roles service
     * @param groupsService the groups service
     * @param passwordEncoder the encoder to use to encode passwords
     */
    public UsersServiceImpl(UsersRepository userRepository, UserRolesRepository userRolesRepo,
                            UserGroupsRepository userGroupsRepo, RolesService rolesService,
                            GroupsService groupsService, PasswordEncoder passwordEncoder) {
        this.userRepo = Objects.requireNonNull(userRepository, "userRepository cannot be null");
        this.userRolesRepo = Objects.requireNonNull(userRolesRepo, "userRolesRepo cannot be null");
        this.userGroupsRepo = Objects.requireNonNull(userGroupsRepo, "userGroupsRepo cannot be null");
        this.rolesService = Objects.requireNonNull(rolesService, "rolesService cannot be null");
        this.groupsService = Objects.requireNonNull(groupsService, "groupsService cannot be null");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "passwordEncoder cannot be null");
    }

    @Override
    public Mono<User> get(String username) {
        return userRepo.getForUsername(username);
    }

    @Override
    public Mono<User> getRequired(String username) {
        return get(username).switchIfEmpty(Mono.error(() -> new ObjectNotFoundException("No user found for " + username)));
    }

    @Override
    public Flux<User> getUsers(int maxResults, long offset) {
        return userRepo.getAll(maxResults, offset);
    }

    @Override
    public Mono<User> create(User user) {
        if (!StringUtils.hasLength(user.getPassword()))
            throw new IllegalArgumentException("Password cannot be empty");
        return Mono.just(user)
                .subscribeOn(Schedulers.parallel())
                .map(u -> u.toBuilder().password(passwordEncoder.encode(u.getPassword())).build())
                .flatMap(userRepo::insert)
                .onErrorMap(DataIntegrityViolationException.class, e -> new DuplicateException("A user with username " +
                        user.getUsername() + " already exists", e));
    }

    @Override
    public Mono<Void> delete(long userId) {
        return userRolesRepo.deleteUserRolesForUser(userId)
                .then(userGroupsRepo.deleteUserGroupsForUser(userId))
                .then(userRepo.delete(userId));
    }

    @Override
    public Mono<Void> delete(String username) {
        //noinspection ConstantConditions
        return get(username)
                .flatMap(user -> delete(user.getId()));
    }

    @Override
    public Mono<Void> grantRoleToUser(String username, String applicationName, String roleName) {
        return getRequired(username)
                .flatMap(user -> rolesService.getRequired(applicationName, roleName)
                        .flatMap(role -> userRolesRepo.insert(user.getId(), role.getId())));
    }

    @Override
    public Mono<Void> removeRoleFromUser(String username, String applicationName, String roleName) {
        return getRequired(username)
                .flatMap(user -> rolesService.getRequired(applicationName, roleName)
                        .flatMap(role -> userRolesRepo.delete(user.getId(), role.getId())));
    }

    @Override
    public Mono<Void> grantGroupToUser(String username, String groupName) {
        return getRequired(username)
                .flatMap(user -> groupsService.getRequired(groupName)
                        .flatMap(group -> userGroupsRepo.insert(user.getId(), group.getId())));
    }

    @Override
    public Mono<Void> removeGroupFromUser(String username, String groupName) {
        return getRequired(username)
                .flatMap(user -> groupsService.getRequired(groupName)
                        .flatMap(group -> userGroupsRepo.delete(user.getId(), group.getId())));
    }

    @Override
    public Mono<Void> updateUserPassword(String username, String currentPassword, String newPassword) {
        Objects.requireNonNull(currentPassword, "currentPassword cannot be null");
        Objects.requireNonNull(newPassword, "newPassword cannot be null");

        String newPasswordEncoded = passwordEncoder.encode(newPassword);
        String currentPasswordEncoded = passwordEncoder.encode(currentPassword);

        return getRequired(username)
                .flatMap(user -> userRepo.updatePassword(user.getId(), currentPasswordEncoded, newPasswordEncoded)
                        .onErrorResume(EmptyResultDataAccessException.class,
                                e -> Mono.error(new ObjectNotFoundException("Incorrect current password", e))));
    }
}
