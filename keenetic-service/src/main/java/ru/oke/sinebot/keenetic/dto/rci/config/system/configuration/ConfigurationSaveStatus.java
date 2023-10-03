package ru.oke.sinebot.keenetic.dto.rci.config.system.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.rci.common.ActionEntityStatusArray;

/**
 * Класс описывает объект API Keenetic OS, который приходит как часть ответа на запрос сохранения конфигурации.
 * Оборачивает статус выполнения операции сохранения
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ConfigurationSaveStatus {
    private ActionEntityStatusArray save;
}
