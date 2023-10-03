package ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Объект данного класса может использоваться как часть запроса в API Keenetic OS для оперативного включения сеанса WPS
 * на точке доступа
 *
 * @author k.oshoev
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class WpsSessionCreation {
    /*
       объект, имитирующий нажатие физической кнопки на корпусе роутера
     */
    private Button button;
}
