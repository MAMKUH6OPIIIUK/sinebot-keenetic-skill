package ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.rci.common.ActionEntityStatusArray;

/**
 * Объект данного класса описывает часть ответа, получаемого от роутера Keenetic на запрос включения сеанса WPS
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WpsSessionStatus {
    private ActionEntityStatusArray button;
}
