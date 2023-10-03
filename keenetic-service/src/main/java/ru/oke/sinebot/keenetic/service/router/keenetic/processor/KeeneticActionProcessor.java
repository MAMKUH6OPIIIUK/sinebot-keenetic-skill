package ru.oke.sinebot.keenetic.service.router.keenetic.processor;

import ru.oke.sinebot.keenetic.dto.api.action.ActionResponseDto;
import ru.oke.sinebot.keenetic.dto.types.ActionType;

/**
 * Интерфейс для обработчиков заданий на выполнение простых действий (без параметров) над роутерами Keenetic.
 * Начинается с префикса "Keenetic", т.к. обработчики данного типа гарантировано предназначены только
 * для роутеров Keenetic. У других производителей управление свойствами может зависеть не только от типа свойства, но и
 * модели модели устройства и/или версии прошивки
 *
 * @author k.oshoev
 */
public interface KeeneticActionProcessor {
    /**
     * Метод возвращает поддерживаемый данным процессором тип действия
     *
     * @return поддерживаемый тип свойства
     */
    ActionType getProcessedActionType();

    /**
     * Метод для непосредственного выполнения действия с определенным устройством. Не должен выбрасывать никаких
     * исключений - при возникновении любых ошибок они должны быть обернуты в соответствующее поле результата
     *
     * @param domainName FQDN роутера
     * @return результат выполнения действия
     */
    ActionResponseDto executeAction(String domainName);
}
