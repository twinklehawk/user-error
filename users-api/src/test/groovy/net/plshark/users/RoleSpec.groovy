package net.plshark.users


import spock.lang.Specification

class RoleSpec extends Specification {

    def "default constructor sets id to empty and sets name"() {
        when:
        net.plshark.users.model.Role role = new net.plshark.users.model.Role("name")

        then:
        role.id != null
        !role.id.present
        role.name == "name"
    }

    def "two-arg constructor sets id and name"() {
        when:
        net.plshark.users.model.Role role = new net.plshark.users.model.Role(12, "name")

        then:
        role.id.present
        role.id.get() == 12
        role.name == "name"
    }

    def "name cannot be set to null"() {
        when:
        new net.plshark.users.model.Role(null)

        then:
        thrown(NullPointerException)

        when:
        net.plshark.users.model.Role role = new net.plshark.users.model.Role("name")
        role.name = null

        then:
        thrown(NullPointerException)
    }
}
