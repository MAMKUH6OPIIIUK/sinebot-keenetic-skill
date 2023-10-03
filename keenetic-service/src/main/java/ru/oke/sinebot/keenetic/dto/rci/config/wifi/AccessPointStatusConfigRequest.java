package ru.oke.sinebot.keenetic.dto.rci.config.wifi;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс частично описывает объект API Keenetic OS, при помощи которого выполняются настройки точек доступа Wi-Fi
 * Описывает одну атомарную операцию административного включения\выключения точки доступа
 * Объекты данного класса могут в чистом виде отправляться на следующий endpoint роутера Keenetic:
 * rci/interface
 *
 * @author k.oshoev
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessPointStatusConfigRequest {
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
     административный статус точки доступа (включена\выключена)
     */
    private Boolean up;
}
