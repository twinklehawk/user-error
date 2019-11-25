package net.plshark.users.auth.jwt;

import java.util.Set;
import javax.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Authentication implementation for use with JWT authentication
 */
@AutoValue
public abstract class JwtAuthenticationToken implements Authentication {

    @Nullable
    public abstract String getUsername();

    @Nullable
    public abstract String getToken();

    @Override
    public abstract boolean isAuthenticated();

    @Override
    public abstract ImmutableSet<GrantedAuthority> getAuthorities();

    @Override
    public String getCredentials() {
        return getToken();
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return getUsername();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException("Cannot change authenticated state after creation");
    }

    @Override
    public String getName() {
        return getUsername();
    }

    /**
     * @return a new Builder for creating instances of JwtAuthenticationToken
     */
    public static Builder builder() {
        return new AutoValue_JwtAuthenticationToken.Builder()
                .authenticated(false);
    }

    /**
     * Creates instances of JwtAuthenticationToken
     */
    @AutoValue.Builder
    public static abstract class Builder {

        /**
         * Set the username for the token to use
         * @param username the username
         * @return this builder
         */
        public abstract Builder username(String username);

        /**
         * Set the JWT for the token to use
         * @param token the JWT
         * @return this builder
         */
        public abstract Builder token(String token);

        /**
         * Set whether the token should be treated as authenticated, defaults to false
         * @param authenticated if the token is authenticate
         * @return this builder
         */
        public abstract Builder authenticated(boolean authenticated);

        abstract ImmutableSet.Builder<GrantedAuthority> authoritiesBuilder();

        public abstract Builder authorities(Set<GrantedAuthority> authorities);

        public abstract JwtAuthenticationToken build();

        /**
         * Add an authority to the authorities list for the token
         * @param authority the authority
         * @return this builder
         */
        public Builder authority(String authority) {
            return authority(new SimpleGrantedAuthority(authority));
        }

        public Builder authority(GrantedAuthority authority) {
            authoritiesBuilder().add(authority);
            return this;
        }
    }
}
