package ru.oke.sinebot.keenetic.dto.rci.config.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.rci.config.system.configuration.ConfigurationSaveStatus;

/**
 * Класс описывает объект, получаемый от API Keenetic OS в ответ на запрос сохранения конфигурации
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ConfigurationSaveResponse {
    private ConfigurationSaveStatus configuration;
}
