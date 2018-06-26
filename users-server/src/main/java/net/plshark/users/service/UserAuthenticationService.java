package net.plshark.users.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

/**
 * Interface for authenticating a user and retrieving details on the authenticated user
 */
public interface UserAuthenticationService extends ReactiveUserDetailsService {

    /**
     * Get the user ID of the authenticated user
     * @param auth the authenticated user
     * @return the user ID
     */
    Mono<Long> getUserIdForAuthentication(Authentication auth);
}
