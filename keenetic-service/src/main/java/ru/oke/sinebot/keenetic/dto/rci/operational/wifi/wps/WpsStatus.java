package ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Статус WPS на точке доступа
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WpsStatus {
    private WpsStatusInfo wps;
}
