package net.plshark.users.model

import spock.lang.Specification

class UserSpec extends Specification {

    def 'constructor args in right order'() {
        when:
        def user = User.create(321L, 'name', 'pass')

        then:
        user.id == 321L
        user.username == 'name'
        user.password == 'pass'
    }

    def 'ID is nullable'() {
        when:
        def user = User.create('user', 'pass')

        then:
        user.id == null
    }
}
