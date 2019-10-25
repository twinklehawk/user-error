package net.plshark.users.auth.throttle.impl

import java.util.concurrent.TimeUnit

import spock.lang.Specification

class LoginAttemptServiceImplSpec extends Specification {

    LoginAttemptServiceImpl service = new LoginAttemptServiceImpl(5, 1)

    def "failing authentication from the same IP less than the max attempts does not block the IP"() {
        when:
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test1", "192.168.1.2")
        service.onLoginFailed("test2", "192.168.1.2")
        service.onLoginFailed("test3", "192.168.1.2")
        service.onLoginFailed("test4", "192.168.1.2")

        then:
        !service.isIpBlocked("192.168.1.2")
    }

    def "failing authentication from the same IP more than the max attempts blocks the IP"() {
        when:
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test1", "192.168.1.2")
        service.onLoginFailed("test2", "192.168.1.2")
        service.onLoginFailed("test3", "192.168.1.2")
        service.onLoginFailed("test4", "192.168.1.2")
        service.onLoginFailed("test5", "192.168.1.2")

        then:
        service.isIpBlocked("192.168.1.2")
    }

    def "failing authentication for the same username less than the max attempts does not block the username"() {
        when:
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.3")
        service.onLoginFailed("test", "192.168.1.4")
        service.onLoginFailed("test", "192.168.1.5")
        service.onLoginFailed("test", "192.168.1.6")

        then:
        !service.isUsernameBlocked("test")
    }

    def "failing authentication for the same username more than the max attempts blocks the username"() {
        when:
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.3")
        service.onLoginFailed("test", "192.168.1.4")
        service.onLoginFailed("test", "192.168.1.5")
        service.onLoginFailed("test", "192.168.1.6")
        service.onLoginFailed("test", "192.168.1.7")

        then:
        service.isUsernameBlocked("test")
    }

    def "failed login attempts expire after the configured amount of time"() {
        LoginAttemptServiceImpl service = new LoginAttemptServiceImpl(5, 1, TimeUnit.SECONDS)

        when:
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.2")
        service.onLoginFailed("test", "192.168.1.2")
        Thread.sleep(2 * 1000)

        then:
        !service.isIpBlocked("192.168.1.2")
        !service.isUsernameBlocked("test")
    }
}
