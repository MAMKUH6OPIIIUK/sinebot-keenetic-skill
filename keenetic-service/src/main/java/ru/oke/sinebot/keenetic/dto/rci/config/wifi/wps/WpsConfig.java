package ru.oke.sinebot.keenetic.dto.rci.config.wifi.wps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Класс частично описывает объект API Keenetic OS, при помощи которого конфигурируются сеансы WPS на точках
 * доступа Wi-Fi.
 * Описаны не все допустимые для настройки свойства, а только лишь интересные в рамках данного проекта: административное
 * включение функционала WPS (только включение, не запуск сеанса)
 *
 * @author k.oshoev
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class WpsConfig {
    /*
        Разрешен ли запуск сеансов WPS на точке доступа
     */
    private Boolean enabled;
}
