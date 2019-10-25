package net.plshark.users.auth.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Authentication implementation for use with JWT authentication
 */
public class JwtAuthenticationToken implements Authentication {

    private final String username;
    private final String token;
    private final boolean authenticated;
    private final Collection<GrantedAuthority> authorities;

    private JwtAuthenticationToken(String username, String token, boolean authenticated, Collection<GrantedAuthority> authorities) {
        this.username = username;
        this.token = token;
        this.authenticated = authenticated;
        this.authorities = Objects.requireNonNull(authorities, "authorities cannot be null");
    }

    /**
     * @return a new Builder for creating instances of JwtAuthenticationToken
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return username;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException("Cannot change authenticated state after creation");
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public String toString() {
        return "JwtAuthenticationToken{" +
                "username='" + username + '\'' +
                ", authenticated=" + authenticated +
                ", authorities=" + authorities +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JwtAuthenticationToken that = (JwtAuthenticationToken) o;
        return authenticated == that.authenticated &&
                Objects.equals(username, that.username) &&
                Objects.equals(token, that.token) &&
                Objects.equals(authorities, that.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, token, authenticated, authorities);
    }

    /**
     * Creates instances of JwtAuthenticationToken
     */
    public static class Builder {

        private String username;
        private String token;
        private boolean authenticated;
        private final Collection<GrantedAuthority> authorities = new ArrayList<>();

        private Builder() {

        }

        /**
         * Set the username for the token to use
         * @param username the username
         * @return this builder
         */
        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        /**
         * Set the JWT for the token to use
         * @param token the JWT
         * @return this builder
         */
        public Builder withToken(String token) {
            this.token = token;
            return this;
        }

        /**
         * Set whether the token should be treated as authenticated, defaults to false
         * @param authenticated if the token is authenticate
         * @return this builder
         */
        public Builder withAuthenticated(boolean authenticated) {
            this.authenticated = authenticated;
            return this;
        }

        /**
         * Add an authority to the authorities list for the token
         * @param authority the authority
         * @return this builder
         */
        public Builder withAuthority(String authority) {
            Objects.requireNonNull(authority, "authority cannot be null");
            authorities.add(new SimpleGrantedAuthority(authority));
            return this;
        }

        /**
         * Add a group of authorities to the authorities list for the token
         * @param authorities the authorities
         * @return this builder
         */
        public Builder withAuthorities(Collection<String> authorities) {
            authorities.forEach(this::withAuthority);
            return this;
        }

        /**
         * Build a JwtAuthenticationToken
         * @return the token
         */
        public JwtAuthenticationToken build() {
            return new JwtAuthenticationToken(username, token, authenticated, authorities);
        }
    }
}
