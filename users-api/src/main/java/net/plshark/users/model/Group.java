package net.plshark.users.model;

import com.google.auto.value.AutoValue;
import reactor.util.annotation.Nullable;

@AutoValue
public abstract class Group {

    public static Group create(String name) {
        return create(null, name);
    }

    public static Group create(@Nullable Long id, String name) {
        return new AutoValue_Group(id, name);
    }

    @Nullable
    public abstract Long getId();

    public abstract String getName();
}
