package net.plshark.auth.throttle;

/**
 * Service to track successful and failed login attempts and if requests from an IP address or for a
 * username should be blocked
 */
public interface LoginAttemptService {

    /**
     * Record a successful login attempt
     * @param username the username that was logged in
     * @param clientIp the source IP address for the attempt
     */
    void onLoginSucceeded(String username, String clientIp);

    /**
     * Record an unsuccessful login attempt
     * @param username the username that attempted to logged in
     * @param clientIp the source IP address for the attempt
     */
    void onLoginFailed(String username, String clientIp);

    /**
     * Check if login attempts for a username should be blocked
     * @param username the username
     * @return if attempts should be blocked
     */
    boolean isUsernameBlocked(String username);

    /**
     * Check if login attempts from an IP address should be blocked
     * @param clientIp the IP address
     * @return if attempts should be blocked
     */
    boolean isIpBlocked(String clientIp);
}
