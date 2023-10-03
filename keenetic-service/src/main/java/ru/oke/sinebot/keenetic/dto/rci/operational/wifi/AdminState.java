package ru.oke.sinebot.keenetic.dto.rci.operational.wifi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Перечисление допустимых административных статусов точки доступа, получаемых при оперативном запросе информации о ней
 *
 * @author k.oshoev
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum AdminState {
    UP("up"),
    DOWN("down");

    @JsonValue
    private final String state;

    @JsonCreator
    public static AdminState from(final String state) {
        for (AdminState knownState : AdminState.values()) {
            if (knownState.state.equalsIgnoreCase(state)) {
                return knownState;
            }
        }
        return null;
    }
}
