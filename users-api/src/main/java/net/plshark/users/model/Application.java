package net.plshark.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

/**
 * Data for an application
 */
@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = Application.ApplicationBuilder.class)
public class Application {

    /** the ID, can be null if not saved yet */
    @Nullable
    private final Long id;
    @NonNull
    private final String name;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApplicationBuilder {

    }
}
