package ru.oke.sinebot.keenetic.dto.rci.config.system.configuration;

import lombok.Getter;
import lombok.ToString;

/**
 * Объект данного класса описывает часть запроса к API Keenetic OS для сохранения конфигурации устройства
 *
 * @author k.oshoev
 */
@Getter
@ToString
public class ConfigurationSave {
    private boolean save;

    /**
     * Конструктор по умолчанию. Устанавливает единственное допустимое значение для поля save
     */
    public ConfigurationSave() {
        this.save = true;
    }
}
