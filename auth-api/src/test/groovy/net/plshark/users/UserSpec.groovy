package net.plshark.users

import net.plshark.users.User
import spock.lang.Specification

class UserSpec extends Specification {

    def "username cannot be null in constructor"() {
        when:
        new User(null, "pass")

        then:
        thrown(NullPointerException)
    }

    def "constructor without ID sets ID to empty"() {
        User user

        when:
        user = new User("name", "pass")

        then:
        user.id != null
        !user.id.present
    }

    def "cannot set username to null through setter"() {
        User user = new User("user", "pass")

        when:
        user.setUsername(null)

        then:
        thrown(NullPointerException)
    }
}
