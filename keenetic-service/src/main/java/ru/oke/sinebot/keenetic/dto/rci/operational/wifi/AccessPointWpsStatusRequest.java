package ru.oke.sinebot.keenetic.dto.rci.operational.wifi;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps.WpsStatusRequest;

/**
 * Класс частично описывает объект API Keenetic OS, при помощи которого выполняются оперативные действия (не влияющие
 * на конфигурацию устройства) с точками доступа Wi-Fi.
 * Описывает только интересующие в рамках данного проекта функции - запуск сеанса WPS на точке доступа.
 * Объекты данного класса могут в чистом виде отправляться на следующий endpoint роутера Keenetic:
 * rci/show/interface
 *
 * @author k.oshoev
 */
@Getter
@Setter
@ToString
public class AccessPointWpsStatusRequest {
    /*
     полное наименование точки доступа для настройки, либо её alias.
     полные наименования должны представлять собой следующую строку:
     WifiMaster<индекс Wi-Fi мастера>/AccessPoint<индекс точки доступа>, где по умолчнию индексы мастера отвечают за
     частоту, на которой работает Wi-Fi: 0 - 2.4 ГГц, 1 - 5 ГГц, а индексы точек доступа за тип точки доступа: 0 -
     основная домашняя точка доступа, 1 - гостевая точка доступа
     */
    @NotNull
    private String name;

    private WpsStatusRequest wps;

    public AccessPointWpsStatusRequest(String name) {
        this.name = name;
        this.wps = new WpsStatusRequest();
    }
}
