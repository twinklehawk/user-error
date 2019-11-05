package net.plshark;

import java.time.OffsetDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import reactor.util.annotation.Nullable;

/**
 * Response containing information about an exception
 */
@AutoValue
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = AutoValue_ErrorResponse.Builder.class)
public abstract class ErrorResponse {

    public static Builder builder() {
        return new AutoValue_ErrorResponse.Builder()
                .timestamp(OffsetDateTime.now());
    }

    /**
     * @return the date and time when the exception happened
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public abstract OffsetDateTime getTimestamp();

    /**
     * @return the response status code
     */
    public abstract int getStatus();

    /**
     * @return the response status description
     */
    public abstract String getStatusDetail();

    /**
     * @return the error detail message
     */
    @Nullable
    public abstract String getMessage();

    /**
     * @return the path of the request that caused the error
     */
    public abstract String getPath();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract static class Builder {

        public abstract Builder timestamp(OffsetDateTime timestamp);

        public abstract Builder status(int status);

        public abstract Builder statusDetail(String statusDetail);

        public abstract Builder message(String message);

        public abstract Builder path(String path);

        public abstract ErrorResponse build();
    }
}
