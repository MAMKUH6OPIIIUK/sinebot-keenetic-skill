package ru.oke.sinebot.yandex.dto.capability.mode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ModeInstance {
    INPUT_SOURCE("input_source"),
    PROGRAM("program");

    @JsonValue
    private final String instance;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ModeInstance from(String instance) {
        for (ModeInstance knownInstance : ModeInstance.values()) {
            if (knownInstance.instance.equalsIgnoreCase(instance)) {
                return knownInstance;
            }
        }
        return null;
    }
}
