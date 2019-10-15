package net.plshark.users.model

import spock.lang.Specification

class GroupSpec extends Specification {

    def 'constructor args in correct order'() {
        when:
        def group = Group.create(12L, 'test-group')

        then:
        group.id == 12L
        group.name == 'test-group'
    }

    def 'ID is nullable'() {
        when:
        def group = Group.create('test')

        then:
        group.id == null
        group.name == 'test'
    }
}
