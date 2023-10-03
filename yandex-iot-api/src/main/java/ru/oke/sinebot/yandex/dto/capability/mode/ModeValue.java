package ru.oke.sinebot.yandex.dto.capability.mode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModeValue {
    ONE("one"),
    TWO("two"),
    THREE("three"),
    FOUR("four");

    @JsonValue
    private final String value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ModeValue from(String value) {
        for (ModeValue knownValue : ModeValue.values()) {
            if (knownValue.value.equalsIgnoreCase(value)) {
                return knownValue;
            }
        }
        return null;
    }
}
