package net.plshark.users.model

import spock.lang.Specification

class UserInfoSpec extends Specification {

    def 'constructor args in right order'() {
        when:
        def info = UserInfo.create(123, 'username')

        then:
        info.id == 123
        info.username == 'username'
    }

    def 'initializes fields from user'() {
        when:
        def info = UserInfo.fromUser(User.create(123L, 'user', 'pass'))

        then:
        info.id == 123
        info.username == 'user'
    }
}
