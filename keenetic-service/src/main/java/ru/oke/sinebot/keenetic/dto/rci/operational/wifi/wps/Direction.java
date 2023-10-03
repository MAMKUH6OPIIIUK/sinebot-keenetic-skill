package ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Некоторые устройства Keenetic могут работать не только как роутер, но и как ретранслятор, поэтому WPS может работать
 * в 2 направлениях:
 * - передача настроек клиентам роутера
 * - получение настроек Wi-Fi текущим роутером от другой точки доступа
 *
 * @author k.oshoev
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum Direction {
    SEND("send"),
    RECEIVE("receive");

    @JsonValue
    private final String direction;

    @JsonCreator
    public static Direction from(final String direction) {
        for (Direction knownDirection : Direction.values()) {
            if (knownDirection.direction.equalsIgnoreCase(direction)) {
                return knownDirection;
            }
        }
        return null;
    }
}
