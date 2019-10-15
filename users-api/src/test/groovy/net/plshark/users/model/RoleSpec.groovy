package net.plshark.users.model

import spock.lang.Specification

class RoleSpec extends Specification {

    def 'constructor args in right order'() {
        when:
        def role = Role.create(123L, 'role', 'app')

        then:
        role.id == 123L
        role.name == 'role'
        role.application == 'app'
    }

    def 'ID is nullable'() {
        when:
        def role = Role.create('name', 'app')

        then:
        role.id == null
        role.name == 'name'
        role.application == 'app'
    }
}
