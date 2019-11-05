package net.plshark

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class ErrorResponseSpec extends Specification {

    def "serialized to correct JSON"() {
        ObjectMapper mapper = new ObjectMapper()
        mapper.findAndRegisterModules()
        OffsetDateTime dt = ZonedDateTime.of(2011, 9, 20, 11, 57, 30, 12, ZoneId.of("America/New_York"))
            .toOffsetDateTime()

        when:
        String str = mapper.writeValueAsString(ErrorResponse.builder()
                .timestamp(dt)
                .status(200)
                .statusDetail('status')
                .message('great')
                .path('/path')
                .build())

        then:
        str == '{"timestamp":"2011-09-20T11:57:30.000000012-04:00","status":200,"statusDetail":"status","message":"great","path":"/path"}'
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
