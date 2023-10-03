package ru.oke.sinebot.keenetic.dto.rci.operational.wifi;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Класс описывает объект для оперативного запроса информации о точке доступа через API Keenetic OS
 * Может быть передан на следующий endpoint роутера:
 * rci/show/interface
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AccessPointStatusRequest {
    /*
     полное наименование точки доступа для настройки, либо её alias.
     полные наименования должны представлять собой следующую строку:
     WifiMaster<индекс Wi-Fi мастера>/AccessPoint<индекс точки доступа>, где по умолчнию индексы мастера отвечают за
     частоту, на которой работает Wi-Fi: 0 - 2.4 ГГц, 1 - 5 ГГц, а индексы точек доступа за тип точки доступа: 0 -
     основная домашняя точка доступа, 1 - гостевая точка доступа
     */
    @NotNull
    private String name;
}
