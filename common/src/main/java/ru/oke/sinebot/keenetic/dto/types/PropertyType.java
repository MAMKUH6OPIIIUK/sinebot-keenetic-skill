package ru.oke.sinebot.keenetic.dto.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum PropertyType {
    STATE("state"),
    WPS("wps");

    @JsonValue
    private final String type;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PropertyType from(String type) {
        for (PropertyType knownType : PropertyType.values()) {
            if (knownType.type.equalsIgnoreCase(type)) {
                return knownType;
            }
        }
        return null;
    }
}
