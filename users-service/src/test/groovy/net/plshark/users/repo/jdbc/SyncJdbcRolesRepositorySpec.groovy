package net.plshark.users.repo.jdbc


import spock.lang.Specification

class SyncJdbcRolesRepositorySpec extends Specification {

    def "constructor does not accept null args"() {
        when:
        new JdbcRolesRepository(null)

        then:
        thrown(NullPointerException)
    }
}
