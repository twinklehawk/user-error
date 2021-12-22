package net.plshark.usererror.error

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.OffsetDateTime

/**
 * Response containing information about an error
 */
data class ErrorResponse(
    /** the date and time when the error happened  */
    @JsonFormat(shape = JsonFormat.Shape.STRING) val timestamp: OffsetDateTime = OffsetDateTime.now(),
    /** the response status code  */
    val status: Int,
    /** the response status description  */
    val statusDetail: String,
    /** the error detail message  */
    val message: String?,
    /** the path of the request that caused the error  */
    val path: String
)
