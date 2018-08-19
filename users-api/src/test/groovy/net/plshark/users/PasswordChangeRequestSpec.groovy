package net.plshark.users


import spock.lang.*

class PasswordChangeRequestSpec extends Specification {

    def "constructor sets correct fields"() {
        when:
        net.plshark.users.model.PasswordChangeRequest request = net.plshark.users.model.PasswordChangeRequest.create("current pass", "new pass")

        then:
        request.currentPassword == "current pass"
        request.newPassword == "new pass"
    }
}
