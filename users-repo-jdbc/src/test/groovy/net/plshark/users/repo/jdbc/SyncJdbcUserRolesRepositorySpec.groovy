package net.plshark.users.repo.jdbc

import net.plshark.users.repo.jdbc.JdbcUserRolesRepository
import spock.lang.Specification

class SyncJdbcUserRolesRepositorySpec extends Specification {

    def "constructor does not accept null args"() {
        when:
        new JdbcUserRolesRepository(null)

        then:
        thrown(NullPointerException)
    }
}
