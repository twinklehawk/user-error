package net.plshark.users.auth.throttle.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.plshark.users.auth.throttle.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default LoginAttemptService implementation
 */
public class LoginAttemptServiceImpl implements LoginAttemptService {

    /** The default max failed authentication attempts */
    public static final int DEFAULT_MAX_ATTEMPTS = 10;
    /** The default number of minutes before tracked authentication attempts reset */
    public static final long DEFAULT_LOGIN_TIME_FRAME = 8 * 60; // 8 hours
    private static final Logger log = LoggerFactory.getLogger(LoginAttemptServiceImpl.class);

    private final int maxAttempts;
    private final long timeFrameMinutes;

    private final LoadingCache<String, AtomicInteger> cache;

    /**
     * Create a new instance
     */
    public LoginAttemptServiceImpl() {
        this(DEFAULT_MAX_ATTEMPTS, DEFAULT_LOGIN_TIME_FRAME);
    }

    /**
     * Create a new instance
     * @param maxAttempts the maximum failed authentication attempts in the time frame before future
     *            attempts will be blocked
     * @param timeFrameMinutes the number of minutes before resetting the failed attempt count after a
     *            failed login attempt
     */
    public LoginAttemptServiceImpl(int maxAttempts, long timeFrameMinutes) {
        this(maxAttempts, timeFrameMinutes, TimeUnit.MINUTES);
    }

    /**
     * Create a new instance
     * @param maxAttempts the maximum failed authentication attempts in the time frame before future
     *            attempts will be blocked
     * @param timeFrame the amount of time before resetting the failed attempt count after a
     *            failed login attempt
     * @param timeFrameUnit the units for {@code timeFrame}
     */
    LoginAttemptServiceImpl(int maxAttempts, long timeFrame, TimeUnit timeFrameUnit) {
        this.maxAttempts = maxAttempts;
        this.timeFrameMinutes = TimeUnit.MINUTES.convert(timeFrame, timeFrameUnit);
        cache = Caffeine.newBuilder().expireAfterWrite(timeFrame, timeFrameUnit)
                .build(key -> new AtomicInteger(0));
    }

    @Override
    public void onLoginFailed(String username, String clientIp) {
        incrementAttempts(username);
        incrementAttempts(clientIp);
    }

    @Override
    public boolean isUsernameBlocked(String username) {
        return cache.get(username).get() > maxAttempts;
    }

    @Override
    public boolean isIpBlocked(String clientIp) {
        return cache.get(clientIp).get() > maxAttempts;
    }

    /**
     * Increment the login attempts for a key
     * @param key the key
     */
    private void incrementAttempts(String key) {
        int attempts = cache.get(key).incrementAndGet();
        if (attempts > maxAttempts)
            log.warn("login attempts for {} blocked for {} minutes", key, timeFrameMinutes);
    }
}
