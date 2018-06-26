package net.plshark.users.repo.jdbc

import java.sql.ResultSet

import net.plshark.users.Role
import net.plshark.users.repo.jdbc.RoleRowMapper
import spock.lang.Specification

class RoleRowMapperSpec extends Specification {

    RoleRowMapper mapper = new RoleRowMapper()

    def "row mapped to note"() {
        ResultSet rs = Mock()
        rs.getLong("id") >> 5L
        rs.getString("name") >> "admin"

        when:
        Role role = mapper.mapRow(rs, 1)

        then:
        role.id.get() == 5
        role.name == "admin"
    }
}
