package ru.oke.sinebot.keenetic.dto.rci.operational.wifi;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps.WpsSessionCreation;

/**
 * Класс частично описывает объект API Keenetic OS, при помощи которого выполняются оперативные действия (не влияющие
 * на конфигурацию устройства) с точками доступа Wi-Fi.
 * Описывает только интересующие в рамках данного проекта функции - запуск сеанса WPS на точке доступа.
 * Объекты данного класса могут в чистом виде отправляться на следующий endpoint роутера Keenetic:
 * rci/interface
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessPointWpsSessionRequest {
    /*
     полное наименование точки доступа для настройки, либо её alias.
     полные наименования должны представлять собой следующую строку:
     WifiMaster<индекс Wi-Fi мастера>/AccessPoint<индекс точки доступа>, где по умолчнию индексы мастера отвечают за
     частоту, на которой работает Wi-Fi: 0 - 2.4 ГГц, 1 - 5 ГГц, а индексы точек доступа за тип точки доступа: 0 -
     основная домашняя точка доступа, 1 - гостевая точка доступа
     */
    @NotNull
    private String name;

    private WpsSessionCreation wps;
}
