package ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Объект данного класса может описывать состояние функционала WPS на точке доступа, получаемое в ответ на запрос
 * статуса при условии, что передано корректное наименование точки доступа
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class WpsStatusInfo {
    /*
    есть ли необходимая конфигурация для работы WPS (как минимум, необходимы настройки PSK security)
    если её нет, то включить функционал WPS и запустить сессию будет невозможно
     */
    private Boolean configured;

    @JsonProperty("auto-self-pin")
    private Boolean autoSelfPin;

    /*
    текущий статус WPS (выключен \ включен \ запущена сессия). Если выключен, то для запуска сессии надо предварительно
    включить. Нет смысла включать, если выключен и не сконфигурирован
     */
    private State status;

    private Direction direction;

    private String mode;

    /*
    длительность работы запущенной сессии WPS в секундах (может отсутствовать)
     */
    private String duration;

    /*
    оставшееся время до завершения запущенной сессии WPS в секундах. Может представлять собой строку "infinite"
     */
    private String left;
}
