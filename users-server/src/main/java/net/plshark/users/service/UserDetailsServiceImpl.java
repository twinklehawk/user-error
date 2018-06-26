package net.plshark.users.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import net.plshark.users.Role;
import net.plshark.users.User;
import net.plshark.users.repo.UserRolesRepository;
import net.plshark.users.repo.UsersRepository;
import reactor.core.publisher.Mono;

/**
 * Implementation of the UserDetailsService
 */
@Named
@Singleton
public class UserDetailsServiceImpl implements UserAuthenticationService {

    private final UsersRepository userRepo;
    private final UserRolesRepository userRolesRepo;

    /**
     * Create a new instance
     * @param userRepo the user repository
     * @param userRolesRepo the user roles repository
     */
    public UserDetailsServiceImpl(UsersRepository userRepo, UserRolesRepository userRolesRepo) {
        this.userRepo = Objects.requireNonNull(userRepo, "userRepository cannot be null");
        this.userRolesRepo = Objects.requireNonNull(userRolesRepo, "userRolesRepo cannot be null");
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepo.getForUsername(username)
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("No matching user for " + username)))
            .flatMap(user -> userRolesRepo.getRolesForUser(user.getId().get())
                .collectList()
                .map(roles -> UserInfo.forUser(user, roles)));
    }

    @Override
    public Mono<Long> getUserIdForAuthentication(Authentication auth) {
        Mono<Long> userId;

        if (auth.getPrincipal() instanceof UserInfo)
            userId = Mono.just(((UserInfo) auth.getPrincipal()).getUserId());
        else
            userId = userRepo.getForUsername(auth.getName())
                .map(user -> user.getId().get());

        return userId;
    }

    /**
     * UserDetails implementation that allows retrieving the user ID
     */
    static class UserInfo extends org.springframework.security.core.userdetails.User {

        private static final long serialVersionUID = -5943477264654485111L;
        private final long userId;

        public UserInfo(long userId, String username, String password,
                Collection<? extends GrantedAuthority> authorities) {
            super(username, password, authorities);
            this.userId = userId;
        }

        public long getUserId() {
            return userId;
        }

        /**
         * Build a UserInfo for a user and its roles
         * @param user the user
         * @param userRoles the user's roles
         * @return the built UserInfo
         */
        public static UserInfo forUser(User user, List<Role> userRoles) {
            Set<GrantedAuthority> authorities = new HashSet<>(userRoles.size());
            userRoles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
            return new UserInfo(user.getId().get(), user.getUsername(), user.getPassword().get(), authorities);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString()).append(": ");
            sb.append("UserID: ").append(this.userId);
            return sb.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + (int) (userId ^ (userId >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            UserInfo other = (UserInfo) obj;
            if (userId != other.userId)
                return false;
            return true;
        }
    }
}
