package net.plshark.auth.model

import spock.lang.Specification

class AccountCredentialsSpec extends Specification {

    def 'constructor args in right order'() {
        when:
        def creds = AccountCredentials.create('user', 'pass')

        then:
        creds.username == 'user'
        creds.password == 'pass'
    }
}
