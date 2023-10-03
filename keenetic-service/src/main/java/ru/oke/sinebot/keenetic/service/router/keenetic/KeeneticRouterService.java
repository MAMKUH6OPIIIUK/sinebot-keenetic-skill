package ru.oke.sinebot.keenetic.service.router.keenetic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oke.sinebot.keenetic.dto.api.action.ActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointChangeStatusDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointStatusDto;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.types.ActionType;
import ru.oke.sinebot.keenetic.dto.types.PropertyType;
import ru.oke.sinebot.keenetic.service.router.RouterService;
import ru.oke.sinebot.keenetic.service.router.keenetic.processor.KeeneticAccessPointPropertyProcessor;
import ru.oke.sinebot.keenetic.service.router.keenetic.processor.KeeneticActionProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Реализация {@link RouterService} для управления роутерами Keenetic.Выполняет координацию действий между различными
 * поддерживаемыми обработчиками заданий на взаимодействие с роутерами Keenetic
 *
 * @author k.oshoev
 */
@Slf4j
@Service
public class KeeneticRouterService implements RouterService {
    private static final String SUPPORTED_VENDOR = "Keenetic";

    private final Map<PropertyType, KeeneticAccessPointPropertyProcessor> propertyProcessors;

    private final Map<ActionType, KeeneticActionProcessor> actionProcessors;


    /**
     * Конструктор для внедрения специально подготовленного RestTemplate, имеющего interceptor
     * {@link KeeneticAuthInterceptor} для выполнения аутентификации
     *
     * @param propertyProcessors список обработчиков заданий на управление свойствами точек доступа
     * @param actionProcessors   список обработчиков заданий на выполнение активных действий с устройствами
     */
    public KeeneticRouterService(List<KeeneticAccessPointPropertyProcessor> propertyProcessors,
                                 List<KeeneticActionProcessor> actionProcessors) {
        this.propertyProcessors = propertyProcessors.stream()
                .collect(Collectors.toMap(KeeneticAccessPointPropertyProcessor::getProcessedPropertyType,
                        Function.identity()));
        this.actionProcessors = actionProcessors.stream()
                .collect(Collectors.toMap(KeeneticActionProcessor::getProcessedActionType, Function.identity()));
    }

    /**
     * Метод возвращает наименование производителя роутеров, для которого подходит данная реализация
     *
     * @return наименование производителя
     */
    @Override
    public String getSupportedVendorName() {
        return SUPPORTED_VENDOR;
    }

    /**
     * Метод для получения статусов всех поддерживаемых управляемых свойств конкретной точки доступа на определенном
     * устройстве.
     *
     * @param accessPoint dto с информацией о точке доступа (адрес устройства, специфичное для производителя
     *                    наименование точки доступа в API роутера, список запрашиваемых параметров)
     * @return список статусов запрошенных свойств
     */
    @Override
    public List<PropertyStatusResponseDto> getAccessPointStatus(AccessPointStatusDto accessPoint) {
        String domainName = accessPoint.getDomainName();
        String interfaceName = accessPoint.getInterfaceName();
        List<PropertyStatusResponseDto> result = new ArrayList<>();
        for (PropertyStatusRequestDto property : accessPoint.getProperties()) {
            KeeneticAccessPointPropertyProcessor propertyProcessor = this.propertyProcessors.get(property.getType());
            if (propertyProcessor == null) {
                result.add(new PropertyStatusResponseDto(property.getType(),
                        new ErrorDto(ErrorCode.INVALID_ACTION,
                                "Операция над неподдерживаемым свойством точки доступа")));
            } else {
                result.add(propertyProcessor.getPropertyStatus(domainName, interfaceName));
            }
        }
        return result;
    }

    /**
     * Метод для назначения статусов всем перечисленным в запросе свойствам конкретной точки доступа на определенном
     * устройстве
     *
     * @param accessPoint dto с информацией о точке доступа (адрес устройства, специфичное для производителя
     *                    наименование точки доступа в API роутера) и списком свойств, для которых необходимо изменить
     *                    статусы
     * @return список результатов изменения каждого из переданных свойств
     */
    @Override
    public List<PropertyChangeStatusResponseDto> setAccessPointStatus(AccessPointChangeStatusDto accessPoint) {
        String domainName = accessPoint.getDomainName();
        String interfaceName = accessPoint.getInterfaceName();
        List<PropertyChangeStatusResponseDto> result = new ArrayList<>();
        for (PropertyChangeStatusRequestDto property : accessPoint.getProperties()) {
            KeeneticAccessPointPropertyProcessor propertyProcessor = this.propertyProcessors.get(property.getType());
            if (propertyProcessor == null) {
                result.add(new PropertyChangeStatusResponseDto(property.getType(),
                        new ErrorDto(ErrorCode.INVALID_ACTION,
                                "Операция над неподдерживаемым свойством точки доступа")));
            } else {
                result.add(propertyProcessor.setPropertyStatus(domainName, interfaceName, property));
            }
        }
        return result;
    }

    /**
     * Метод для выполнения заданного действия на определенном устрйостве
     *
     * @param requestDto dto с информацией об устройстве (обязательно должно быть в наличии доменное имя) и типе
     *                   запрашиваемого действия
     * @return результат выполнения действия на устройстве
     */
    @Override
    public ActionResponseDto executeAction(DeviceActionRequestDto requestDto) {
        ActionType type = requestDto.getAction().getType();
        KeeneticActionProcessor actionProcessor = this.actionProcessors.get(type);
        if (actionProcessor == null) {
            return new ActionResponseDto(type, new ErrorDto(ErrorCode.INVALID_ACTION, "Неподдерживаемая " +
                    "операция над устройством"));
        }
        return actionProcessor.executeAction(requestDto.getDomainName());
    }

}
