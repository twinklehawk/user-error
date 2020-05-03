package net.plshark

import com.fasterxml.jackson.databind.ObjectMapper
import net.plshark.errors.ErrorResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class ErrorResponseSpec {

    @Test
    fun `serialized to correct JSON`() {
        val mapper = ObjectMapper()
        mapper.findAndRegisterModules()
        val dt = ZonedDateTime.of(2011, 9, 20, 11, 57, 30, 12, ZoneId.of("America/New_York"))
            .toOffsetDateTime()
        val str = mapper.writeValueAsString(ErrorResponse(
            timestamp = dt,
            status = 200,
            statusDetail = "status",
            message = "great",
            path = "/path"
        ))

        assertEquals("""{"timestamp":"2011-09-20T11:57:30.000000012-04:00","status":200,"statusDetail":"status","message":"great","path":"/path"}""", str)
    }

    @Test
    fun `deserialized from JSON with correct values`() {
        val mapper = ObjectMapper()
        mapper.findAndRegisterModules()
        val dt = ZonedDateTime.of(2011, 9, 20, 11, 57, 30, 12, ZoneId.of("America/New_York"))
            .toOffsetDateTime()

        val response = mapper.readValue("""{"timestamp":"2011-09-20T11:57:30.000000012-04:00","status":200,"statusDetail":"status","message":"great","path":"/path"}""",
            ErrorResponse::class.java)

        assertEquals("great", response.message)
        assertEquals(200, response.status)
        assertEquals("status", response.statusDetail)
        assertEquals("/path", response.path)
        assertTrue(response.timestamp.isEqual(dt))
    }
}
