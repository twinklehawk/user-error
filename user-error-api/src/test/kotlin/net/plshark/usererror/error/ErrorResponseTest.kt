package net.plshark.usererror.error

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class ErrorResponseTest {

    @Test
    fun `serialized to correct JSON`() {
        val mapper = ObjectMapper()
        mapper.findAndRegisterModules()
        val dt = ZonedDateTime.of(2011, 9, 20, 11, 57, 30, 12, ZoneId.of("America/New_York"))
            .toOffsetDateTime()
        val str = mapper.writeValueAsString(
            net.plshark.usererror.error.ErrorResponse(
                timestamp = dt,
                status = 200,
                statusDetail = "status",
                message = "great",
                path = "/path"
            )
        )

        val expected = "{\"timestamp\":\"2011-09-20T11:57:30.000000012-04:00\",\"status\":200,\"statusDetail\":" +
            "\"status\",\"message\":\"great\",\"path\":\"/path\"}"
        assertEquals(expected, str)
    }

    @Test
    fun `deserialized from JSON with correct values`() {
        val mapper = ObjectMapper()
        mapper.findAndRegisterModules()
        val dt = ZonedDateTime.of(2011, 9, 20, 11, 57, 30, 12, ZoneId.of("America/New_York"))
            .toOffsetDateTime()

        val response = mapper.readValue(
            "{\"timestamp\":\"2011-09-20T11:57:30.000000012-04:00\",\"status\":200," +
                "\"statusDetail\":\"status\",\"message\":\"great\",\"path\":\"/path\"}",
            net.plshark.usererror.error.ErrorResponse::class.java
        )

        assertEquals("great", response.message)
        assertEquals(200, response.status)
        assertEquals("status", response.statusDetail)
        assertEquals("/path", response.path)
        assertTrue(response.timestamp.isEqual(dt))
    }
}
