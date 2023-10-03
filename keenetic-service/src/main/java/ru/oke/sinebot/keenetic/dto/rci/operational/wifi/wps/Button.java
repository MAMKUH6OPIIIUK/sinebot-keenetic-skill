package ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Класс описывает объект, при помощи которого в API Keenetic OS имитируется нажатие кнопки WPS на корпусе роутера
 *
 * @author k.oshoev
 */
@Getter
@AllArgsConstructor
@ToString
public class Button {

    private Direction direction;

}
