package ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps;

import lombok.Getter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.rci.common.EmptyObject;

/**
 * Объект данного класса может использоваться как часть запроса статусов WPS (оперативного и административного) на
 * точке доступа с использованием API Keenetic
 *
 * @author k.oshoev
 */
@Getter
@ToString
public class WpsStatusRequest {
    private final EmptyObject status;

    public WpsStatusRequest() {
        this.status = new EmptyObject();
    }
}
