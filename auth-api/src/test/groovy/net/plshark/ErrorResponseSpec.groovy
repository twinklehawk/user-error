package net.plshark

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

import com.fasterxml.jackson.databind.ObjectMapper

import spock.lang.*

class ErrorResponseSpec extends Specification {

    def "constructor sets correct fields"() {
        when:
        ErrorResponse response = ErrorResponse.create(400, "status", "something happened", "1/2/3")

        then:
        response.status == 400
        response.statusDetail == "status"
        response.message == "something happened"
        response.path == "1/2/3"
        response.timestamp != null
    }

    def "full constructor sets correct fields"() {
        when:
        ErrorResponse response = ErrorResponse.create(OffsetDateTime.now(), 400, "status", "something happened", "1/2/3")

        then:
        response.status == 400
        response.statusDetail == "status"
        response.message == "something happened"
        response.path == "1/2/3"
        response.timestamp != null
    }

    def "serialized to correct JSON"() {
        ObjectMapper mapper = new ObjectMapper()
        mapper.findAndRegisterModules()
        OffsetDateTime dt = ZonedDateTime.of(2011, 9, 20, 11, 57, 30, 12, ZoneId.of("America/New_York"))
            .toOffsetDateTime()

        when:
        String str = mapper.writeValueAsString(ErrorResponse.create(dt, 200, "status", "great", "/path"))

        then:
        str == '{"timestamp":"2011-09-20T11:57:30.000000012-04:00","status":200,"message":"great","path":"/path","statusDetail":"status"}'
    }

    def "deserialized from JSON with correct values"() {
        ObjectMapper mapper = new ObjectMapper()
        mapper.findAndRegisterModules()
        OffsetDateTime dt = ZonedDateTime.of(2011, 9, 20, 11, 57, 30, 12, ZoneId.of("America/New_York"))
            .toOffsetDateTime()

        when:
        ErrorResponse response = mapper.readValue('{"timestamp":"2011-09-20T11:57:30.000000012-04:00","status":200,"statusDetail":"status","message":"great","path":"/path"}',
            ErrorResponse.class)

        then:
        response.message == 'great'
        response.status == 200
        response.statusDetail == 'status'
        response.path == '/path'
        response.timestamp.isEqual(dt)
    }
}
