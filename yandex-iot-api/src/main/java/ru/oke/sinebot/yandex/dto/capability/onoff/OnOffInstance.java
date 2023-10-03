package ru.oke.sinebot.yandex.dto.capability.onoff;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OnOffInstance {
    ON("on");

    @JsonValue
    private final String instance;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static OnOffInstance from(String instance) {
        if (ON.instance.equalsIgnoreCase(instance)) {
            return ON;
        }
        return null;
    }
}
