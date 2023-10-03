package ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.deserializer.WpsStatusDeserializer;
import ru.oke.sinebot.keenetic.dto.rci.common.ActionEntityStatus;

import java.util.List;

/**
 * Объект данного класса является частью ответа API Keenetic OS на запрос статуса WPS на точке доступа.
 * Ответ может содержать либо полученный статус, либо список ошибок получения статуса - оба варианта приходят с
 * одинаковым именем поля в JSON объекте.
 * Для разбора используется кастомный десериализатор
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonDeserialize(using = WpsStatusDeserializer.class)
public class WpsStatusWrapper {
    private WpsStatus wpsStatus;

    private List<ActionEntityStatus> actionStatus;
}
