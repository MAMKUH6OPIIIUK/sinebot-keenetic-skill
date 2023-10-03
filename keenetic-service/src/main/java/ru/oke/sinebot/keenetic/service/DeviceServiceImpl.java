package ru.oke.sinebot.keenetic.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oke.sinebot.keenetic.dto.api.action.ActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionRequestDto;
import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.ActionDto;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceRequestDto;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointChangeStatusDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointStatusDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.types.ActionType;
import ru.oke.sinebot.keenetic.exception.NotFoundException;
import ru.oke.sinebot.keenetic.exception.NotSupportedException;
import ru.oke.sinebot.keenetic.mapper.AccessPointMapper;
import ru.oke.sinebot.keenetic.mapper.DeviceMapper;
import ru.oke.sinebot.keenetic.model.Device;
import ru.oke.sinebot.keenetic.model.Model;
import ru.oke.sinebot.keenetic.model.action.Action;
import ru.oke.sinebot.keenetic.model.wifi.AccessPoint;
import ru.oke.sinebot.keenetic.dto.types.AccessPointType;
import ru.oke.sinebot.keenetic.model.wifi.Property;
import ru.oke.sinebot.keenetic.dto.types.WifiFrequency;
import ru.oke.sinebot.keenetic.repository.DeviceRepository;
import ru.oke.sinebot.keenetic.repository.ModelRepository;
import ru.oke.sinebot.keenetic.service.router.RouterService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис для управления информацией об устройствах пользователей и состоянием этих устройств
 *
 * @author k.oshoev
 */
@Service
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepository;

    private final ModelRepository modelRepository;

    private final DeviceMapper deviceMapper;

    private final AccessPointMapper accessPointMapper;

    private final Map<String, RouterService> routerServices;

    public DeviceServiceImpl(DeviceRepository deviceRepository, ModelRepository modelRepository,
                             DeviceMapper deviceMapper, AccessPointMapper accessPointMapper,
                             List<RouterService> routerServices) {
        this.deviceRepository = deviceRepository;
        this.modelRepository = modelRepository;
        this.deviceMapper = deviceMapper;
        this.accessPointMapper = accessPointMapper;
        this.routerServices = routerServices.stream()
                .collect(Collectors.toMap(RouterService::getSupportedVendorName, Function.identity()));
    }

    /**
     * Метод для добавления нового устройства пользователя в базу данных
     *
     * @param deviceRequestDto объект с информацией об устройстве
     * @return dto с информацией о сохраненном устройстве
     */
    @PreAuthorize(value = "hasAuthority('SCOPE_ROLE_USER')")
    @Override
    @Transactional
    public DeviceResponseDto create(DeviceRequestDto deviceRequestDto) {
        Model deviceModel = this.validateModel(deviceRequestDto.getModelId());
        Device deviceForCreate = this.deviceMapper.mapToDevice(deviceRequestDto, deviceModel);
        Device createdDevice = this.deviceRepository.save(deviceForCreate);
        this.checkConnection(createdDevice);
        return this.deviceMapper.mapToDeviceResponseDto(createdDevice);
    }

    /**
     * Метод для обновления существующего устройства пользователя в базе данных
     *
     * @param deviceRequestDto объект с информацией об устройстве
     */
    @PreAuthorize("hasPermission(#deviceRequestDto.id, 'ru.oke.sinebot.keenetic.model.Device', 'ADMINISTRATION')")
    @Override
    @Transactional
    public void update(DeviceRequestDto deviceRequestDto) {
        Device deviceForUpdate = this.validateDevice(deviceRequestDto.getId());
        Model newDeviceModel = this.validateModel(deviceRequestDto.getModelId());
        this.deviceMapper.mergeDeviceData(deviceForUpdate, deviceRequestDto, newDeviceModel);
        this.deviceRepository.save(deviceForUpdate);
        this.checkConnection(deviceForUpdate);
    }

    @PreAuthorize("hasPermission(#id, 'ru.oke.sinebot.keenetic.model.Device', 'ADMINISTRATION')")
    @Override
    @Transactional(readOnly = true)
    public DeviceResponseDto findById(Long id) {
        Device device = this.validateDevice(id);
        return this.deviceMapper.mapToDeviceResponseDto(device);
    }

    /**
     * Метод для поиска информации обо всех сохраненных устройствах пользователя
     *
     * @param userId идентификатор пользователя
     * @return список устройств пользователя
     */
    @Override
    @Transactional(readOnly = true)
    public List<DeviceResponseDto> findByUserId(Long userId) {
        List<Device> foundDevices = this.deviceRepository.findByUserId(userId);
        return foundDevices.stream()
                .map(this.deviceMapper::mapToDeviceResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Метод для удаления информации об устройстве по его идентификатору
     *
     * @param id идентификатор устройства
     */
    @PreAuthorize("hasPermission(#id, 'ru.oke.sinebot.keenetic.model.Device', 'ADMINISTRATION')")
    @Override
    @Transactional
    public void deleteById(Long id) {
        this.deviceRepository.deleteById(id);
    }

    /**
     * Метод для получения актуальных статусов всех параметров всех точек доступа на заданном устройстве <p>
     * На основе хранимой в базе данных информации об устройстве выбирает нужную реализацию сервиса для
     * непосредственного взаимодействия с роутером. На основе этой же информации конструирует dto с необходимыми данными
     * об устройстве, его точках доступа и их доступных для получения статуса параметрах. Для каждой точки доступа
     * осуществляет обращение к выбранному сервису и собирает результаты в общий ответ
     *
     * @param id идентификатор устройства
     * @return объект со статусами параметров всех поддерживаемых точек доступа на роутере
     */
    @PreAuthorize("hasPermission(#id, 'ru.oke.sinebot.keenetic.model.Device', 'ADMINISTRATION')")
    @Override
    @Transactional(readOnly = true)
    public DeviceStatusResponseDto queryDeviceAccessPointsStatus(Long id) {
        Device device = this.validateDevice(id);
        String vendorName = device.getModel().getVendor().getName();
        RouterService routerService = this.getRouterService(vendorName);
        List<AccessPointStatusResponseDto> accessPointsStatus = new ArrayList<>();
        for (AccessPoint deviceAccessPoint : device.getModel().getAccessPoints()) {
            AccessPointStatusDto statusDto = this.accessPointMapper.mapToStatusDto(deviceAccessPoint,
                    device.getDomainName());
            List<PropertyStatusResponseDto> statusResponse = routerService.getAccessPointStatus(statusDto);
            accessPointsStatus.add(this.accessPointMapper.mapToStatusResponseDto(deviceAccessPoint,
                    statusResponse));
        }
        return new DeviceStatusResponseDto(id, accessPointsStatus);
    }

    /**
     * Метод для назначения статусов указанным в запросе параметрам точек доступа определенного устройства.<p>
     * Выполняет сопоставление переданных в запросе точек доступа и их параметров с данными об устройстве в БД и
     * выбрасывает исключение, если было запрошено изменение статуса несуществующего устройства, несуществующей точки
     * доступа на устройстве или несуществующего свойства точки доступа.
     * На основе корректной и проверенной информации конструируются dto для взаимодействия с сервисом, ответственным за
     * непосредственное взаимодействие с устройством, в целях изменения настроек точек доступа. Результаты собираются в
     * общий ответ.
     * Если в общем результате есть информация хотя бы об одном успешно измененном свойстве, выполняется сохранение
     * конфигурации
     *
     * @param changeStatusDto объект, содержащий информацию о новых статусах свойств точек доступа определенного
     *                       устройства. Может содержать информацию только о некоторых точках доступа устройства или
     *                       только о некоторых свойствах (PATCH). Не должен содержать точек доступа, не относящихся к
     *                       устройству
     * @return результат изменения статусов параметров точек доступа на устройстве
     */
    @PreAuthorize("hasPermission(#changeStatusDto.id, 'ru.oke.sinebot.keenetic.model.Device', 'ADMINISTRATION')")
    @Override
    @Transactional(readOnly = true)
    public DeviceChangeStatusResponseDto changeDeviceAccessPointsStatus(DeviceChangeStatusRequestDto changeStatusDto) {
        Device device = this.validateDevice(changeStatusDto.getId());
        String vendorName = device.getModel().getVendor().getName();
        RouterService routerService = this.getRouterService(vendorName);
        List<AccessPoint> deviceAccessPoints = device.getModel().getAccessPoints();
        List<AccessPointChangeStatusDto> accessPointDtos = new ArrayList<>();
        for (AccessPointChangeStatusRequestDto accessPointRequestDto : changeStatusDto.getAccessPoints()) {
            AccessPoint deviceAccessPoint = this.validateRequestedAccessPoint(accessPointRequestDto,
                    deviceAccessPoints);
            accessPointDtos.add(this.accessPointMapper.mapToChangeStatusDto(accessPointRequestDto,
                    deviceAccessPoint, device.getDomainName()));
        }
        List<AccessPointChangeStatusResponseDto> accessPointStatus = new ArrayList<>();
        for (AccessPointChangeStatusDto statusDto : accessPointDtos) {
            List<PropertyChangeStatusResponseDto> changeStatusResponse = routerService.setAccessPointStatus(statusDto);
            accessPointStatus.add(this.accessPointMapper.mapToChangeStatusResponseDto(statusDto, changeStatusResponse));
        }
        this.saveConfiguration(routerService, device, accessPointStatus);
        return new DeviceChangeStatusResponseDto(device.getId(), accessPointStatus);
    }

    /**
     * Метод для выполнения активного действия на устройстве.<p>
     * Сверяет запрошенное действие с информацией о поддерживаемых для устройства действиях из БД. Выбирает на основе
     * информации об устройстве нужный сервис для взаимодействия с роутером и выполняет само активное действие при
     * помощи выбранного сервиса
     *
     * @param actionRequestDto объект, содержащий информацию о требуемом действии. Должен содержать, как минимум,
     *                         идентификатор устройства и тип вызываемого действия
     * @return результат выполнения действия
     */
    @PreAuthorize("hasPermission(#actionRequestDto.id, 'ru.oke.sinebot.keenetic.model.Device', 'ADMINISTRATION')")
    @Override
    @Transactional(readOnly = true)
    public DeviceActionResponseDto executeAction(DeviceActionRequestDto actionRequestDto) {
        Device device = this.validateDevice(actionRequestDto.getId());
        this.validateAction(actionRequestDto.getAction(), device.getModel().getSupportedActions());
        actionRequestDto.setDomainName(device.getDomainName());
        String vendorName = device.getModel().getVendor().getName();
        RouterService routerService = this.getRouterService(vendorName);
        ActionResponseDto actionResult = routerService.executeAction(actionRequestDto);
        return new DeviceActionResponseDto(device.getId(), actionResult);
    }

    /**
     * Метод для проверки существования устройства в БД по его идентификатору
     *
     * @param id идентификатор устройства
     * @return информация о найденном в БД устройстве, которая может использоваться в других методах
     */
    private Device validateDevice(Long id) {
        return this.deviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Устройство с указанным идентификатором " + id +
                        " не найдено"));
    }

    /**
     * Метод для проверки существования модели устройства в БД по её идентификатору
     *
     * @param modelId идентификатор модели
     * @return информация о модели устройства
     */
    private Model validateModel(Long modelId) {
        return this.modelRepository.findById(modelId)
                .orElseThrow(() -> new NotFoundException("Модель с указанным идентификатором " + modelId +
                        " не найдена"));
    }

    /**
     * Метод выполняет сопоставление точки доступа, указанной в запросе на изменение статуса устройства, со списком
     * точек доступа, привязанных к устройству в БД. При отсутствии запрошенной точки доступа в БД кидает исключение
     *
     * @param requestedAccessPoint информация о точке доступа из запроса на изменение статусов её свойств
     * @param accessPoints         список точке доступа, привязанных к устройству в БД
     * @return информация о найденной в БД точке доступа, совпадающей по типу и частоте с точкой доступа из запроса
     */
    private AccessPoint validateRequestedAccessPoint(AccessPointChangeStatusRequestDto requestedAccessPoint,
                                                     List<AccessPoint> accessPoints) {
        AccessPointType accessPointType = requestedAccessPoint.getType();
        WifiFrequency accessPointBand = requestedAccessPoint.getBand();
        AccessPoint deviceAccessPoint = accessPoints.stream()
                .filter(point -> point.getType() == accessPointType && point.getBand() == accessPointBand)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Не найдена точка доступа с указанными параметрами. " +
                        "Тип: " + accessPointType.getType() + ", band: " + accessPointBand.getBand()));
        for (PropertyChangeStatusRequestDto propertyDto : requestedAccessPoint.getProperties()) {
            this.validateAccessPointProperty(propertyDto, deviceAccessPoint.getProperties());
        }
        return deviceAccessPoint;
    }

    /**
     * Метод сопоставляет управляемое свойство точки доступа из запроса со списком известных свойств той же точки
     * доступа из БД. При отсутствии информации о запрошенном свойстве в БД кидает исключение
     *
     * @param requestedProperty     конкретное свойство конкретной точки доступа из запроса
     * @param accessPointProperties список свойств той же точки доступа в БД
     */
    private void validateAccessPointProperty(PropertyChangeStatusRequestDto requestedProperty,
                                             List<Property> accessPointProperties) {
        accessPointProperties.stream()
                .filter(property -> property.getType() == requestedProperty.getType())
                .findFirst()
                .orElseThrow(() -> new NotSupportedException("Свойство указанного типа \"" +
                        requestedProperty.getType().getType() + "\" не поддерживается"));
    }

    /**
     * Метод выполняет сопоставление переданного в запросе активного действия со списком поддерживаемых устройством
     * активных действий из БД. Кидает исключение, если по информации в БД активное действие не поддерживается
     * устройством
     *
     * @param actionDto        запрошенное активное действие
     * @param supportedActions список поддерживаемых устройством активных действий
     */
    private void validateAction(ActionDto actionDto, List<Action> supportedActions) {
        supportedActions.stream()
                .filter(action -> action.getType() == actionDto.getType())
                .findFirst()
                .orElseThrow(() -> new NotSupportedException("Активное действие указанного типа не поддерживается " +
                        "устройством"));
    }

    /**
     * Метод отвечает за сохранение конфигурации устройства при необходимости:
     * 1. Устойство должно поддерживать сохранение конфигурации
     * 2. Конфирурация устройства должна быть изменена в результате предыдущих действий
     * Не кидает никаких ошибок и ничего не возвращает
     *
     * @param routerService     сервис для взаимодействия с указанным в аргументе device устройством
     * @param device            информация об устройстве и поддерживаемых им активных действиях из БД
     * @param accessPointStatus результаты изменения свойств точек доступа устройства
     */
    private void saveConfiguration(RouterService routerService, Device device,
                                   List<AccessPointChangeStatusResponseDto> accessPointStatus) {
        if (!isActionSupported(device, ActionType.SAVE_CONFIG)) {
            return;
        }
        boolean isConfigChanged = this.existsConfigurationChanges(accessPointStatus);
        if (isConfigChanged) {
            ActionDto saveAction = new ActionDto(ActionType.SAVE_CONFIG);
            DeviceActionRequestDto actionRequest = new DeviceActionRequestDto(device.getId(), device.getDomainName(),
                    saveAction);
            routerService.executeAction(actionRequest);
        }
    }

    /**
     * Метод проверяет, было ли успешно изменено хотя бы одно свойство хотя бы одной точки доступа на устройстве и была
     * ли в результате этого изменения затронута конфигурация (т.е. выполнялись не только оперативные действия)
     *
     * @param accessPointsStatus результаты изменения свойств точек доступа устройства
     * @return true, если конфигурация была изменена
     */
    private boolean existsConfigurationChanges(List<AccessPointChangeStatusResponseDto> accessPointsStatus) {
        for (AccessPointChangeStatusResponseDto status : accessPointsStatus) {
            boolean changed = status.getProperties().stream()
                    .map(PropertyChangeStatusResponseDto::getConfigChanged)
                    .filter(Objects::nonNull)
                    .anyMatch(Boolean::booleanValue);
            if (changed) {
                return true;
            }
        }
        return false;
    }

    /**
     * Метод выполняет проверку возможности подключиться к устройству. Может быть полезен, чтобы сразу отсечь сохранение
     * информации об устройствах пользователя с некорректными реквизитами\недоступным доменным именем
     *
     * @param device устройство пользователя
     * @throws NotFoundException если функционал проверки подключения для устройства поддерживается и подключение не
     *                           удалось выполнить
     */
    private void checkConnection(Device device) {
        if (!isActionSupported(device, ActionType.CHECK_CONNECTION)) {
            return;
        }
        String deviceVendor = device.getModel().getVendor().getName();
        RouterService routerService = this.getRouterService(deviceVendor);
        ActionDto checkConnectAction = new ActionDto(ActionType.CHECK_CONNECTION);
        DeviceActionRequestDto actionRequest = new DeviceActionRequestDto(device.getId(), device.getDomainName(),
                checkConnectAction);
        ActionResponseDto checkResult = routerService.executeAction(actionRequest);
        if (checkResult.getError() != null) {
            throw new NotFoundException("Не удалось выполнить подключение к устройству. Проверьте введенные " +
                    "реквизиты доступа и попробуйте снова");
        }
    }

    /**
     * Метод выполняет проверку на предмет того, поддерживается ли для конкретного устройства заданный тип активного
     * действия
     *
     * @param device     информация об устройстве из БД
     * @param actionType тип активного действия
     * @return true, если действие для устройства поддерживается
     */
    private boolean isActionSupported(Device device, ActionType actionType) {
        return device.getModel().getSupportedActions().stream()
                .anyMatch(action -> action.getType() == actionType);
    }

    /**
     * Метод для выбора конкретной реализации сервиса, отвечающего за непосредственное взаимодействие с роутерами,
     * по наименованию производителя устройства
     *
     * @param vendorName наименование производителя
     * @return найденный сервис для взаимодействия с роутерами указанного производителя
     * @throws NotSupportedException если сервис для указанного производителя не найден
     */
    private RouterService getRouterService(String vendorName) {
        RouterService routerService = this.routerServices.get(vendorName);
        if (routerService == null) {
            throw new NotSupportedException("Не найден сервис для управления роутерами указанного производителя");
        }
        return routerService;
    }
}
