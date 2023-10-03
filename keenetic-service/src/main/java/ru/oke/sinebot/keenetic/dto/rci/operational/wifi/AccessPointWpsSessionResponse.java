package ru.oke.sinebot.keenetic.dto.rci.operational.wifi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps.WpsSessionStatus;

/**
 * Класс описывает объект, получаемый от устройства Keenetic после выполнения запроса на создание WPS сессии на точке
 * доступа
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccessPointWpsSessionResponse {
    private WpsSessionStatus wps;
}
