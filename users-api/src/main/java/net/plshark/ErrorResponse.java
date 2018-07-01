package net.plshark;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response containing information about an exception
 */
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSxxx")
    private final OffsetDateTime timestamp;
    private final int status;
    private final String statusDetail;
    private final String message;
    private final String path;

    private ErrorResponse(@JsonProperty("timestamp") OffsetDateTime timestamp, @JsonProperty("status") int status,
            @JsonProperty("error") String statusDetail, @JsonProperty("message") String message,
            @JsonProperty("path") String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.statusDetail = statusDetail;
        this.message = message;
        this.path = path;
    }

    /**
     * Create an instance with a date and time of now
     * @param status the response status code
     * @param statusDetail the response status description
     * @param message the error detail message
     * @param path the path of the request that caused the error
     * @return the ErrorResponse instance
     */
    public static ErrorResponse create(int status, String statusDetail, String message, String path) {
        return create(OffsetDateTime.now(), status, statusDetail, message, path);
    }

    /**
     * Create an instance
     * @param timestamp the date and time when the exception happened
     * @param status the response status code
     * @param statusDetail the response status description
     * @param message the error detail message
     * @param path the path of the request that caused the error
     * @return the ErrorResponse instance
     */
    @JsonCreator
    public static ErrorResponse create(@JsonProperty("timestamp") OffsetDateTime timestamp,
            @JsonProperty("status") int status, @JsonProperty("error") String statusDetail,
            @JsonProperty("message") String message, @JsonProperty("path") String path) {
        return new ErrorResponse(timestamp, status, statusDetail, message, path);
    }

    /**
     * @return the date and time when the exception happened
     */
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @return the response status code
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return the response status description
     */
    public String getStatusDetail() {
        return statusDetail;
    }

    /**
     * @return the error detail message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the path of the request that caused the error
     */
    public String getPath() {
        return path;
    }
}
