package net.plshark.users

import net.plshark.users.PasswordChangeRequest
import spock.lang.*

class PasswordChangeRequestSpec extends Specification {

    def "constructor sets correct fields"() {
        when:
        PasswordChangeRequest request = PasswordChangeRequest.create("current pass", "new pass")

        then:
        request.currentPassword == "current pass"
        request.newPassword == "new pass"
    }
}
