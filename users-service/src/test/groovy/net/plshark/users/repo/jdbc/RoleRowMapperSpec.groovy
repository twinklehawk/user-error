package net.plshark.users.repo.jdbc

import net.plshark.users.model.Role

import java.sql.ResultSet

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
