package ru.oke.sinebot.keenetic.dto.rci.config.wifi;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.keenetic.dto.rci.config.wifi.wps.WpsConfig;

/**
 * Класс частично описывает объект API Keenetic OS, при помощи которого выполняются настройки точек доступа Wi-Fi
 * Описывает операцию передачи необходимых настроек WPS в конфигурацию точки доступа.
 * Объекты данного класса могут в чистом виде отправляться на следующий endpoint роутера Keenetic:
 * rci/interface
 *
 * @author k.oshoev
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessPointWpsConfigRequest {
    /*
     полное наименование точки доступа для настройки, либо её alias.
     полные наименования должны представлять собой следующую строку:
     WifiMaster<индекс Wi-Fi мастера>/AccessPoint<индекс точки доступа>, где по умолчанию индексы мастера отвечают за
     частоту, на которой работает Wi-Fi: 0 - 2.4 ГГц, 1 - 5 ГГц, а индексы точек доступа за тип точки доступа: 0 -
     основная домашняя точка доступа, 1 - гостевая точка доступа
     */
    @NotNull
    private String name;

    /*
    настройки wps на точке доступа
     */
    private WpsConfig wps;
}
