package net.plshark.users.model

import spock.lang.Specification

class PasswordChangeRequestSpec extends Specification {

    def 'constructor args in right order'() {
        when:
        def request = PasswordChangeRequest.create('pass', 'new')

        then:
        request.currentPassword == 'pass'
        request.newPassword == 'new'
    }
}
