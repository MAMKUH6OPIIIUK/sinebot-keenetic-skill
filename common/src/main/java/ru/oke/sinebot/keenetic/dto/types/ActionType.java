package ru.oke.sinebot.keenetic.dto.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ActionType {
    RELOAD("reload"),
    SAVE_CONFIG("save_config"),
    CHECK_CONNECTION("check_connection");

    @JsonValue
    private final String type;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ActionType from(String type) {
        for (ActionType knownAction : ActionType.values()) {
            if (knownAction.type.equalsIgnoreCase(type)) {
                return knownAction;
            }
        }
        return null;
    }
}
