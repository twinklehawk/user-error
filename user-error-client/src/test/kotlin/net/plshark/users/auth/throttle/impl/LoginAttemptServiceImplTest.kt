package net.plshark.users.auth.throttle.impl

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class LoginAttemptServiceImplTest {

    private val service = LoginAttemptServiceImpl(5, 1)

    @Test
    fun `failing authentication from the same IP less than the max attempts does not block the IP`() {
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test1", "192.168.1.2")
        service.onLoginFailed("test2", "192.168.1.2")
        service.onLoginFailed("test3", "192.168.1.2")
        service.onLoginFailed("test4", "192.168.1.2")

        assertFalse(service.isIpBlocked("192.168.1.2"))
    }

    @Test
    fun `failing authentication from the same IP more than the max attempts blocks the IP`() {
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test1", "192.168.1.2")
        service.onLoginFailed("test2", "192.168.1.2")
        service.onLoginFailed("test3", "192.168.1.2")
        service.onLoginFailed("test4", "192.168.1.2")
        service.onLoginFailed("test5", "192.168.1.2")

        assertTrue(service.isIpBlocked("192.168.1.2"))
    }

    @Test
    fun `failing authentication for the same username less than the max attempts does not block the username`() {
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.3")
        service.onLoginFailed("test", "192.168.1.4")
        service.onLoginFailed("test", "192.168.1.5")
        service.onLoginFailed("test", "192.168.1.6")

        assertFalse(service.isUsernameBlocked("test"))
    }

    @Test
    fun `failing authentication for the same username more than the max attempts blocks the username`() {
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.3")
        service.onLoginFailed("test", "192.168.1.4")
        service.onLoginFailed("test", "192.168.1.5")
        service.onLoginFailed("test", "192.168.1.6")
        service.onLoginFailed("test", "192.168.1.7")

        assertTrue(service.isUsernameBlocked("test"))
    }

    @Test
    fun `failed login attempts expire after the configured amount of time`() {
        val service = LoginAttemptServiceImpl(5, 1, TimeUnit.SECONDS)

        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.2")
        Thread.sleep(2 * 1000)

        assertFalse(service.isIpBlocked("192.168.1.2"))
        assertFalse(service.isUsernameBlocked("test"))
    }
}
