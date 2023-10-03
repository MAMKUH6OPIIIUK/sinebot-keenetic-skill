package ru.oke.sinebot.yandex.dto.capability;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CapabilityType {
    ON_OFF("devices.capabilities.on_off"),
    MODE("devices.capabilities.mode");

    @JsonValue
    private final String type;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CapabilityType from(String type) {
        for (CapabilityType knownType : CapabilityType.values()) {
            if (knownType.type.equalsIgnoreCase(type)) {
                return knownType;
            }
        }
        return null;
    }
}
