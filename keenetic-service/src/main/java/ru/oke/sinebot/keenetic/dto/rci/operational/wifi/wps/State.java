package ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Перечисление допустимых статусов функционала WPS на точке доступа. Выражает как административный статус
 * (enabled\disabled), так и оперативный - active (который по совместительству означает и административный enabled)
 *
 * @author k.oshoev
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum State {
    DISABLED("disabled"),
    ENABLED("enabled"),
    ACTIVE("active");

    @JsonValue
    private final String status;

    @JsonCreator
    public static State from(final String status) {
        for (State knownState : State.values()) {
            if (knownState.status.equalsIgnoreCase(status)) {
                return knownState;
            }
        }
        return null;
    }
}
