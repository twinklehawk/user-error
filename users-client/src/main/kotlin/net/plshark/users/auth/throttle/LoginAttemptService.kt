package net.plshark.users.auth.throttle

/**
 * Service to track successful and failed login attempts and if requests from an IP address or for a
 * username should be blocked
 */
interface LoginAttemptService {
    /**
     * Record an unsuccessful login attempt
     * @param username the username that attempted to logged in
     * @param clientIp the source IP address for the attempt
     */
    fun onLoginFailed(username: String, clientIp: String)

    /**
     * Check if login attempts for a username should be blocked
     * @param username the username
     * @return if attempts should be blocked
     */
    fun isUsernameBlocked(username: String): Boolean

    /**
     * Check if login attempts from an IP address should be blocked
     * @param clientIp the IP address
     * @return if attempts should be blocked
     */
    fun isIpBlocked(clientIp: String): Boolean
}