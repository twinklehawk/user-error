package net.plshark.users.model

import spock.lang.Specification

class GroupSpec extends Specification {

    def 'ID is nullable'() {
        when:
        def group = Group.builder()
                .name('test')
                .build()

        then:
        group.id == null
        group.name == 'test'
    }
}
