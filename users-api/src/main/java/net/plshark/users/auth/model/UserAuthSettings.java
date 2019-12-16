package net.plshark.users.auth.model;

import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = UserAuthSettings.UserAuthSettingsBuilder.class)
public class UserAuthSettings {

    @Nullable
    private final Long id;
    @Nullable
    private final Long userId;
    @JsonProperty("refresh_token_enabled") @Builder.Default
    private final boolean refreshTokenEnabled = true;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserAuthSettingsBuilder {

    }
}
