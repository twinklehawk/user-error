package net.plshark.users.repo.jdbc

import net.plshark.users.repo.jdbc.JdbcRolesRepository
import spock.lang.Specification

class SyncJdbcRolesRepositorySpec extends Specification {

    def "constructor does not accept null args"() {
        when:
        new JdbcRolesRepository(null)

        then:
        thrown(NullPointerException)
    }
}
