package net.plshark.users.repo.jdbc

import net.plshark.users.model.User

import java.sql.ResultSet

import spock.lang.Specification

class UserRowMapperSpec extends Specification {

    UserRowMapper mapper = new UserRowMapper()

    def "row mapped to user"() {
        ResultSet rs = Mock()
        rs.getLong("id") >> 5L
        rs.getString("username") >> "admin"
        rs.getString("password") >> "54321"

        when:
        User user = mapper.mapRow(rs, 1)

        then:
        user.id.get() == 5
        user.username == "admin"
        user.password.get() == "54321"
    }
}
