package net.plshark.users.auth.throttle.impl

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import net.plshark.users.auth.throttle.LoginAttemptService
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Default LoginAttemptService implementation
 */
class LoginAttemptServiceImpl(
    private val maxAttempts: Int = DEFAULT_MAX_ATTEMPTS,
    timeFrame: Long = DEFAULT_TIME_FRAME,
    timeFrameUnit: TimeUnit = TimeUnit.MINUTES
) : LoginAttemptService {

    private val timeFrameMinutes: Long = TimeUnit.MINUTES.convert(timeFrame, timeFrameUnit)
    private val cache: LoadingCache<String, AtomicInteger> = Caffeine.newBuilder()
        .expireAfterWrite(timeFrame, timeFrameUnit)
        .build { AtomicInteger(0) }

    override fun onLoginFailed(username: String, clientIp: String) {
        incrementAttempts(username)
        incrementAttempts(clientIp)
    }

    override fun isUsernameBlocked(username: String): Boolean {
        return cache[username]!!.get() > maxAttempts
    }

    override fun isIpBlocked(clientIp: String): Boolean {
        return cache[clientIp]!!.get() > maxAttempts
    }

    /**
     * Increment the login attempts for a key
     * @param key the key
     */
    private fun incrementAttempts(key: String) {
        val attempts = cache[key]!!.incrementAndGet()
        if (attempts > maxAttempts)
            log.warn("login attempts for {} blocked for {} minutes", key, timeFrameMinutes)
    }

    companion object {
        private val log = LoggerFactory.getLogger(LoginAttemptServiceImpl::class.java)
    }
}

private const val DEFAULT_TIME_FRAME = 8L * 60L
private const val DEFAULT_MAX_ATTEMPTS = 10
