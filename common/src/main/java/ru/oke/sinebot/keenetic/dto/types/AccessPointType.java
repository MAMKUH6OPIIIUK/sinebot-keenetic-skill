package ru.oke.sinebot.keenetic.dto.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Перечисление управляемых типов точек доступа: домашние и гостевые
 *
 * @author k.oshoev
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum AccessPointType {
    HOME("home"),
    GUEST("guest");

    @JsonValue
    private final String type;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AccessPointType from(String type) {
        for (AccessPointType knownType : AccessPointType.values()) {
            if (knownType.type.equalsIgnoreCase(type)) {
                return knownType;
            }
        }
        return null;
    }
}
