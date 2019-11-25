package net.plshark.users.auth.jwt;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableSet;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Authentication implementation for use with JWT authentication
 */
@Value
@Builder(toBuilder = true)
public class JwtAuthenticationToken implements Authentication {

    @Nullable
    private final String username;
    @Nullable
    private final String token;
    private final boolean authenticated;
    @Singular
    private final ImmutableSet<GrantedAuthority> authorities;

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

    public static class JwtAuthenticationTokenBuilder {

        public JwtAuthenticationTokenBuilder authorityName(String authority) {
            return authority(new SimpleGrantedAuthority(authority));
        }
    }
}
