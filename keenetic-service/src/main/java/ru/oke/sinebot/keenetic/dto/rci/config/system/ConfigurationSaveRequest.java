package ru.oke.sinebot.keenetic.dto.rci.config.system;

import lombok.Getter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.rci.config.system.configuration.ConfigurationSave;

/**
 * Данный класс описывает объект, который можно использовать в API Keenetic OS для сохранения конфигурации (копирования
 * running-config в startup-config). Может использоваться для фиксации успешных изменений конфигурации. Если не
 * используется, любые изменения конфигурации устройства будут утеряны после перезагрузки устройства
 * Объект данного класса может в чистом виде передаваться на следующий endpoint роутера Keenetic:
 * rci/system
 *
 * @author k.oshoev
 */
@Getter
@ToString
public class ConfigurationSaveRequest {
    private ConfigurationSave configuration;

    /**
     * Конструктор по умолчанию. Для создания корректного объекта не требуется передача каких-либо параметров
     */
    public ConfigurationSaveRequest() {
        this.configuration = new ConfigurationSave();
    }
}
