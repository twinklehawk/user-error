package net.plshark.users.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.plshark.users.model.Role;
import net.plshark.users.model.User;
import net.plshark.users.repo.UserGroupsRepository;
import net.plshark.users.repo.UserRolesRepository;
import net.plshark.users.repo.UsersRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Implementation of the UserDetailsService
 */
@Component
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UsersRepository userRepo;
    private final UserRolesRepository userRolesRepo;
    private final UserGroupsRepository userGroupsRepo;

    /**
     * Create a new instance
     * @param userRepo the user repository
     * @param userRolesRepo the user roles repository
     * @param userGroupsRepo the user groups repository
     */
    public UserDetailsServiceImpl(UsersRepository userRepo, UserRolesRepository userRolesRepo,
                                  UserGroupsRepository userGroupsRepo) {
        this.userRepo = Objects.requireNonNull(userRepo, "userRepository cannot be null");
        this.userRolesRepo = Objects.requireNonNull(userRolesRepo, "userRolesRepo cannot be null");
        this.userGroupsRepo = Objects.requireNonNull(userGroupsRepo, "userGroupsRepo cannot be null");
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepo.getForUsernameWithPassword(username)
            .switchIfEmpty(Mono.error(() -> new UsernameNotFoundException("No matching user for " + username)))
            .flatMap(user -> userRolesRepo.getRolesForUser(user.getId())
                    .mergeWith(userGroupsRepo.getGroupRolesForUser(user.getId()))
                    .collectList()
                    .map(roles -> buildUserDetails(user, roles)));
    }

    /**
     * Build a UserDetails for a User and its Roles
     * @param user the user
     * @param roles the user's roles
     * @return the UserDetails
     */
    private UserDetails buildUserDetails(User user, List<Role> roles) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(Objects.requireNonNull(user.getPassword()))
                .authorities(roles.stream()
                        .map(this::buildGrantedAuthority)
                        .collect(Collectors.toSet()))
                .build();
    }

    /**
     * Build a GrantedAuthority from a Role
     * @param role the role
     * @return the granted authority
     */
    private GrantedAuthority buildGrantedAuthority(Role role) {
        return new SimpleGrantedAuthority("ROLE_" + role.getName());
    }
}
