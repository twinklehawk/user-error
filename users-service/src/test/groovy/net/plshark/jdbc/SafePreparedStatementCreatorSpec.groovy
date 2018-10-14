package net.plshark.jdbc

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.core.PreparedStatementSetter

import spock.lang.Specification

class SafePreparedStatementCreatorSpec extends Specification {

    PreparedStatementCreator creator = Mock()
    PreparedStatementSetter setter = Mock()
    SafePreparedStatementCreator safe = new SafePreparedStatementCreator(creator, setter)

    def "an open PreparedStatement is returned when no exceptions are thrown"() {
        PreparedStatement stmt = Mock()
        creator.createPreparedStatement(_) >> stmt

        when:
        PreparedStatement result = safe.createPreparedStatement(Mock(Connection))

        then:
        result != null
        0 * stmt.close()
    }

    def "the PreparedStatement is closed if an SQLException is thrown by the PreparedStatementSetter"() {
        PreparedStatement stmt = Mock()
        creator.createPreparedStatement(_) >> stmt
        setter.setValues(_) >> { throw new SQLException() }

        when:
        safe.createPreparedStatement(Mock(Connection))

        then:
        thrown(SQLException)
        1 * stmt.close()
    }

    def "the PreparedStatement is closed if a RuntimeException is thrown by the PreparedStatementSetter"() {
        PreparedStatement stmt = Mock()
        creator.createPreparedStatement(_) >> stmt
        setter.setValues(_) >> { throw new RuntimeException() }

        when:
        safe.createPreparedStatement(Mock(Connection))

        then:
        thrown(RuntimeException)
        1 * stmt.close()
    }

    def "an exception thrown by PreparedStatement.close() is caught and suppressed"() {
        PreparedStatement stmt = Mock()
        creator.createPreparedStatement(_) >> stmt
        setter.setValues(_) >> { throw new RuntimeException() }
        stmt.close() >> { throw new SQLException() }

        when:
        safe.createPreparedStatement(Mock(Connection))

        then:
        RuntimeException e = thrown()
        e.getSuppressed().length == 1
        e.getSuppressed()[0].class == SQLException.class
    }
}
