package ru.oke.sinebot.keenetic.service.router.keenetic.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.oke.sinebot.keenetic.config.RestConfig;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.rci.config.wifi.AccessPointWpsConfigRequest;
import ru.oke.sinebot.keenetic.dto.rci.config.wifi.AccessPointWpsConfigResponse;
import ru.oke.sinebot.keenetic.dto.rci.config.wifi.wps.WpsConfig;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.AccessPointWpsSessionRequest;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.AccessPointWpsSessionResponse;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.AccessPointWpsStatusRequest;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.AccessPointWpsStatusResponse;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps.Button;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps.Direction;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps.State;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps.WpsSessionCreation;
import ru.oke.sinebot.keenetic.exception.NotFoundException;
import ru.oke.sinebot.keenetic.mapper.PropertyStatusMapper;
import ru.oke.sinebot.keenetic.dto.types.PropertyType;
import ru.oke.sinebot.keenetic.service.router.keenetic.KeeneticAuthInterceptor;
import ru.oke.sinebot.keenetic.service.router.keenetic.KeeneticRciUtils;

/**
 * Обработчик заданий на управление функционалом WPS на точке доступа, осуществляющий непосредственное
 * взаимодействие с роутером Keenetic через rci API
 *
 * @author k.oshoev
 */
@Slf4j
@Service
public class KeeneticAccessPointWpsPropertyProcessor implements KeeneticAccessPointPropertyProcessor {
    private static final PropertyType PROCESSED_TYPE = PropertyType.WPS;

    private final RestTemplate restTemplate;

    private final PropertyStatusMapper propertyStatusMapper;

    /**
     * Конструктор для внедрения специально подготовленного RestTemplate, имеющего interceptor
     * {@link KeeneticAuthInterceptor} для выполнения аутентификации
     *
     * @param restTemplate         подготовленный через конфигурацию bean RestTemplate
     * @param propertyStatusMapper маппер статусов свойств точек доступа. Используется для приведения ответов API
     *                             Keenetic OS к объектам, используемым в API приложения
     */
    public KeeneticAccessPointWpsPropertyProcessor(@Qualifier(RestConfig.KEENETIC_REST_TEMPLATE)
                                                           RestTemplate restTemplate,
                                                   PropertyStatusMapper propertyStatusMapper) {
        this.restTemplate = restTemplate;
        this.propertyStatusMapper = propertyStatusMapper;
    }

    /**
     * Метод возвращает поддерживаемый данным процессором тип свойства точки доступа. Процессор управляет
     * функционалом WPS на точке доступа
     *
     * @return поддерживаемый тип свойства
     */
    @Override
    public PropertyType getProcessedPropertyType() {
        return PROCESSED_TYPE;
    }

    /**
     * Метод для получения информации о статусе свойства типа WPS (статус сессии WPS на точке доступа). Выполняет
     * обработку исключений
     *
     * @param domainName    FQDN роутера
     * @param interfaceName специфичное для производителя наименование точки доступа в API роутера
     * @return dto с информацией о статусе сессии WPS, либо ошибке, воспрепятствовавшей его получению
     */
    @Override
    public PropertyStatusResponseDto getPropertyStatus(String domainName, String interfaceName) {
        try {
            AccessPointWpsStatusResponse response = this.getWpsStatusRci(domainName, interfaceName);
            return this.propertyStatusMapper.mapToPropertyStatusResponseDto(response);
        } catch (HttpClientErrorException | ResourceAccessException e) {
            log.error("Ошибка подключения к устройству \"{}\"", domainName, e);
            ErrorDto errorDto = new ErrorDto(ErrorCode.DEVICE_UNREACHABLE, e.getMessage());
            return new PropertyStatusResponseDto(PropertyType.WPS, errorDto);
        } catch (NotFoundException e) {
            log.error("Устройство с именем \"{}\" не найдено: {}", domainName, e.getMessage());
            ErrorDto errorDto = new ErrorDto(ErrorCode.DEVICE_NOT_FOUND, e.getMessage());
            return new PropertyStatusResponseDto(PropertyType.WPS, errorDto);
        } catch (Throwable e) {
            log.error("Неожиданная ошибка при взаимодействии с устройством \"{}\"", domainName, e);
            ErrorDto errorDto = new ErrorDto(ErrorCode.INTERNAL_ERROR, e.getMessage());
            return new PropertyStatusResponseDto(PropertyType.WPS, errorDto);
        }
    }

    /**
     * Метод для управления статусом сессии WPS на точке доступа. Роутеры Keenetic не поддерживают принудительное
     * завершение сессии, поэтому попытки выключить WPS будут неуспешными
     *
     * @param domainName     FQDN роутера
     * @param interfaceName  специфичное для производителя наименование точки доступа в API роутера
     * @param propertyStatus dto с назначаемым статусом
     * @return dto с информацией о том, удалось ли корректно завершить операцию и было ли изменено свойство
     */
    @Override
    public PropertyChangeStatusResponseDto setPropertyStatus(String domainName, String interfaceName,
                                                             PropertyChangeStatusRequestDto propertyStatus) {
        try {
            return this.manageWpsStatus(domainName, interfaceName,
                    propertyStatus.isEnabled());
        } catch (HttpClientErrorException | ResourceAccessException e) {
            log.error("Ошибка подключения к устройству \"{}\"", domainName, e);
            ErrorDto errorDto = new ErrorDto(ErrorCode.DEVICE_UNREACHABLE, e.getMessage());
            return new PropertyChangeStatusResponseDto(PROCESSED_TYPE, errorDto);
        } catch (NotFoundException e) {
            log.error("Устройство с именем \"{}\" не найдено: {}", domainName, e.getMessage());
            ErrorDto errorDto = new ErrorDto(ErrorCode.DEVICE_NOT_FOUND, e.getMessage());
            return new PropertyChangeStatusResponseDto(PROCESSED_TYPE, errorDto);
        } catch (Throwable e) {
            log.error("Неожиданная ошибка при взаимодействии с устройством \"{}\"", domainName, e);
            ErrorDto errorDto = new ErrorDto(ErrorCode.INTERNAL_ERROR, e.getMessage());
            return new PropertyChangeStatusResponseDto(PROCESSED_TYPE, errorDto);
        }
    }

    /**
     * Метод выполняет непосредственное обращение к rci API Keenetic OS для получения статуса функционала WPS на точке
     * доступа. Не обрабатывает исключения
     *
     * @param domainName    FQDN роутера
     * @param interfaceName специфичное для производителя наименование точки доступа в API роутера
     * @return статус функционала WPS, если его удалось получить (устройство доступно, реквизиты подходят и т.д.)
     */
    private AccessPointWpsStatusResponse getWpsStatusRci(String domainName, String interfaceName) {
        AccessPointWpsStatusRequest requestBody = new AccessPointWpsStatusRequest(interfaceName);
        RequestEntity<AccessPointWpsStatusRequest> request = new RequestEntity<>(requestBody, null,
                HttpMethod.POST, KeeneticRciUtils.buildInterfaceInformationUrl(domainName));
        ResponseEntity<AccessPointWpsStatusResponse> response = this.restTemplate.exchange(request,
                AccessPointWpsStatusResponse.class);
        return response.getBody();
    }

    /**
     * Метод для выполнения изменения статуса функционала WPS на точке доступа только при возможности и необходимости:
     * предварительно проверяет текущий статус WPS и в случае, если он соответствует требуемому, не делает никаких
     * изменений. Если для WPS нет необходимых настроек безопасности на точке доступа, либо выполняется операция
     * выключения wps, будет возвращена ошибка.
     *
     * @param domainName    FQDN роутера
     * @param interfaceName специфичное для производителя наименование точки доступа в API роутера
     * @param enabled       должно ли быть включено свойство
     * @return статус операции, если её удалось корректно завершить (устройство доступно, реквизиты подходят и т.д.)
     */
    private PropertyChangeStatusResponseDto manageWpsStatus(String domainName, String interfaceName, boolean enabled) {
        AccessPointWpsStatusResponse statusBefore = this.getWpsStatusRci(domainName, interfaceName);
        PropertyStatusResponseDto statusBeforeDto = this.propertyStatusMapper
                .mapToPropertyStatusResponseDto(statusBefore);
        if (statusBeforeDto.getError() != null) {
            return new PropertyChangeStatusResponseDto(PROCESSED_TYPE, statusBeforeDto.getError());
        } else if (statusBeforeDto.getEnabled().equals(enabled)) {
            return new PropertyChangeStatusResponseDto(PROCESSED_TYPE, false);
        } else if (!statusBefore.getWps().getWpsStatus().getWps().getConfigured()) {
            ErrorDto error = new ErrorDto(ErrorCode.NOT_SUPPORTED_IN_CURRENT_MODE,
                    "Функционал WPS для точки доступа не настроен");
            return new PropertyChangeStatusResponseDto(PROCESSED_TYPE, error);
        } else if (enabled) {
            return this.enableWps(domainName, interfaceName, statusBefore.getWps().getWpsStatus().getWps().getStatus());
        } else {
            ErrorDto errorDto = new ErrorDto(ErrorCode.INVALID_ACTION, "Принудительное завершение сессии " +
                    "WPS не поддерживается роутерами Keenetic. До завершения сессии осталось (в секундах): " +
                    statusBefore.getWps().getWpsStatus().getWps().getLeft());
            return new PropertyChangeStatusResponseDto(PROCESSED_TYPE, errorDto);
        }
    }

    /**
     * Метод для включения функционала WPS. При необходимости выполняет 2 атомарные операции: административное включение
     * WPS и оперативный запуск сессии WPS. Если административные действия не требуются, то будет возвращен результат с
     * информацией о том, что свойство изменено, но конфигурация осталась без изменений (осуществлены только
     * оперативные действия)
     *
     * @param domainName    FQDN роутера
     * @param interfaceName специфичное для производителя наименование точки доступа в API роутера
     * @param currentState  текущий статус WPS для выявления факта о том, что он административно выключен
     * @return статус операции, если её удалось корректно завершить (устройство доступно, реквизиты подходят и т.д.)
     */
    private PropertyChangeStatusResponseDto enableWps(String domainName, String interfaceName, State currentState) {
        boolean configChanged = false;
        if (currentState == State.DISABLED) {
            PropertyChangeStatusResponseDto adminChangeResult = this.enableWpsAdministrativelyRci(domainName,
                    interfaceName);
            if (adminChangeResult.getError() != null) {
                return adminChangeResult;
            }
            configChanged = true;
        }
        PropertyChangeStatusResponseDto sessionStartResponse = this.startWpSessionRci(domainName, interfaceName);
        sessionStartResponse.setConfigChanged(configChanged);
        return sessionStartResponse;
    }

    /**
     * Метод выполняет непосредственное обращение к rci API Keenetic OS для административного включения функционала WPS
     * на точке доступа. Метод производит манипуляции над конфигурацией устройства
     *
     * @param domainName    FQDN роутера
     * @param interfaceName специфичное для производителя наименование точки доступа в API роутера
     * @return статус операции, если её удалось корректно завершить (устройство доступно, реквизиты подходят и т.д.)
     */
    private PropertyChangeStatusResponseDto enableWpsAdministrativelyRci(String domainName, String interfaceName) {
        AccessPointWpsConfigRequest requestBody = new AccessPointWpsConfigRequest(interfaceName,
                new WpsConfig(true));
        RequestEntity<AccessPointWpsConfigRequest> request = new RequestEntity<>(requestBody, null,
                HttpMethod.POST, KeeneticRciUtils.buildInterfaceManagementUrl(domainName));
        ResponseEntity<AccessPointWpsConfigResponse> response = this.restTemplate.exchange(request,
                AccessPointWpsConfigResponse.class);
        AccessPointWpsConfigResponse responseBody = response.getBody();
        return this.propertyStatusMapper.mapToPropertyChangeStatusResponseDto(responseBody);
    }

    /**
     * Метод выполняет непосредственное обращение к rci API Keenetic OS для запуска сессии WPS на точке доступа путем
     * имитации нажатия кнопки на корпусе роутера. Метод производит только оперативные действия и не меняет конфигурацию
     *
     * @param domainName    FQDN роутера
     * @param interfaceName специфичное для производителя наименование точки доступа в API роутера
     * @return статус операции, если её удалось корректно завершить (устройство доступно, реквизиты подходят и т.д.)
     */
    private PropertyChangeStatusResponseDto startWpSessionRci(String domainName, String interfaceName) {
        AccessPointWpsSessionRequest requestBody = new AccessPointWpsSessionRequest(interfaceName,
                new WpsSessionCreation(new Button(Direction.SEND)));
        RequestEntity<AccessPointWpsSessionRequest> request = new RequestEntity<>(requestBody, null,
                HttpMethod.POST, KeeneticRciUtils.buildInterfaceManagementUrl(domainName));
        ResponseEntity<AccessPointWpsSessionResponse> response = this.restTemplate.exchange(request,
                AccessPointWpsSessionResponse.class);
        AccessPointWpsSessionResponse responseBody = response.getBody();
        return this.propertyStatusMapper.mapToPropertyChangeStatusResponseDto(responseBody);
    }
}
