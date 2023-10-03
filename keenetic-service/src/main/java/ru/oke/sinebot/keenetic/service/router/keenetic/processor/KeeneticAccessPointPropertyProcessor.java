package ru.oke.sinebot.keenetic.service.router.keenetic.processor;

import ru.oke.sinebot.keenetic.dto.api.status.PropertyChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.types.PropertyType;

/**
 * Интерфейс для обработчиков заданий на упровление простыми свойствами точки доступа (которые могут быть либо включены,
 * либо выключены). Начинается с префикса "Keenetic", т.к. обработчики данного типа гарантировано предназначены только
 * для роутеров Keenetic. У других производителей управление свойствами может зависеть не только от типа свойства, но и
 * модели модели устройства и/или версии прошивки
 *
 * @author k.oshoev
 */
public interface KeeneticAccessPointPropertyProcessor {
    /**
     * Метод возвращает поддерживаемый данным процессором тип свойства точки доступа
     *
     * @return поддерживаемый тип свойства
     */
    PropertyType getProcessedPropertyType();

    /**
     * Метод для получения информации о статусе поддерживаемого свойства (включено\выключено). <p>
     * Метод не должен выбрасывать никаких исключений - при возникновении ошибок они должны быть обернуты в
     * соответствующее поле результата
     *
     * @param domainName    FQDN роутера
     * @param interfaceName специфичное для производителя наименование точки доступа в API роутера
     * @return dto с информацией статусе свойства, либо ошибке, воспрепятствовавшей его получению
     */
    PropertyStatusResponseDto getPropertyStatus(String domainName, String interfaceName);

    /**
     * Метод для назначения статуса свойству
     * Метод не должен выбрасывать никаких исключений - при возникновении ошибок они должны быть обернуты в
     * соответствующее поле результата
     *
     * @param domainName     FQDN роутера
     * @param interfaceName  специфичное для производителя наименование точки доступа в API роутера
     * @param propertyStatus dto с назначаемым статусом
     * @return dto с информацией о том, удалось ли корректно завершить операцию и было ли изменено свойство
     */
    PropertyChangeStatusResponseDto setPropertyStatus(String domainName, String interfaceName,
                                                      PropertyChangeStatusRequestDto propertyStatus);
}
