package net.plshark.users


import spock.lang.Specification

class UserSpec extends Specification {

    def "username cannot be null in constructor"() {
        when:
        new net.plshark.users.model.User(null, "pass")

        then:
        thrown(NullPointerException)
    }

    def "constructor without ID sets ID to empty"() {
        net.plshark.users.model.User user

        when:
        user = new net.plshark.users.model.User("name", "pass")

        then:
        user.id != null
        !user.id.present
    }

    def "cannot set username to null through setter"() {
        net.plshark.users.model.User user = new net.plshark.users.model.User("user", "pass")

        when:
        user.setUsername(null)

        then:
        thrown(NullPointerException)
    }
}
