package net.plshark.users.model

import spock.lang.Specification

class UserSpec extends Specification {

    def 'ID is nullable'() {
        when:
        def user = User.builder().username('user').password('pass').build()

        then:
        user.id == null
    }

    def 'password is nullable'() {
        when:
        def user = User.builder().id(1).username('user').build()

        then:
        user.password == null
    }
}
