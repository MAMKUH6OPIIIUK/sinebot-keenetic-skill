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
import ru.oke.sinebot.keenetic.dto.rci.config.wifi.AccessPointStatusConfigRequest;
import ru.oke.sinebot.keenetic.dto.rci.config.wifi.AccessPointStatusConfigResponse;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.AccessPointStatusRequest;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.AccessPointStatusResponse;
import ru.oke.sinebot.keenetic.exception.NotFoundException;
import ru.oke.sinebot.keenetic.mapper.PropertyStatusMapper;
import ru.oke.sinebot.keenetic.dto.types.PropertyType;
import ru.oke.sinebot.keenetic.service.router.keenetic.KeeneticAuthInterceptor;
import ru.oke.sinebot.keenetic.service.router.keenetic.KeeneticRciUtils;

/**
 * Обработчик заданий на управление административным статусом точки доступа, осуществляющий непосредственное
 * взаимодействие с роутером Keenetic через rci API
 *
 * @author k.oshoev
 */
@Slf4j
@Service
public class KeeneticAccessPointStatePropertyProcessor implements KeeneticAccessPointPropertyProcessor {
    private static final PropertyType PROCESSED_TYPE = PropertyType.STATE;

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
    public KeeneticAccessPointStatePropertyProcessor(@Qualifier(RestConfig.KEENETIC_REST_TEMPLATE)
                                                             RestTemplate restTemplate,
                                                     PropertyStatusMapper propertyStatusMapper) {
        this.restTemplate = restTemplate;
        this.propertyStatusMapper = propertyStatusMapper;
    }

    /**
     * Метод возвращает поддерживаемый данным процессором тип свойства точки доступа. Процессор управляет
     * административным статусом точки доступа
     *
     * @return поддерживаемый тип свойства
     */
    @Override
    public PropertyType getProcessedPropertyType() {
        return PROCESSED_TYPE;
    }

    /**
     * Метод для получения информации о статусе свойства типа STATE (административный статус точки доступа). Выполняет
     * обработку исключений
     *
     * @param domainName    FQDN роутера
     * @param interfaceName специфичное для производителя наименование точки доступа в API роутера
     * @return dto с информацией об административном статусе, либо ошибке, воспрепятствовавшей его получению
     */
    @Override
    public PropertyStatusResponseDto getPropertyStatus(String domainName, String interfaceName) {
        try {
            AccessPointStatusResponse response = this.getAdminStatusRci(domainName, interfaceName);
            return this.propertyStatusMapper.mapToPropertyStatusResponseDto(response);
        } catch (HttpClientErrorException | ResourceAccessException e) {
            log.error("Ошибка подключения к устройству \"{}\"", domainName, e);
            ErrorDto errorDto = new ErrorDto(ErrorCode.DEVICE_UNREACHABLE, e.getMessage());
            return new PropertyStatusResponseDto(PROCESSED_TYPE, errorDto);
        } catch (NotFoundException e) {
            log.error("Устройство с именем \"{}\" не найдено: {}", domainName, e.getMessage());
            ErrorDto errorDto = new ErrorDto(ErrorCode.DEVICE_NOT_FOUND, e.getMessage());
            return new PropertyStatusResponseDto(PROCESSED_TYPE, errorDto);
        } catch (Throwable e) {
            log.error("Неожиданная ошибка при взаимодействии с устройством \"{}\"", domainName, e);
            ErrorDto errorDto = new ErrorDto(ErrorCode.INTERNAL_ERROR, e.getMessage());
            return new PropertyStatusResponseDto(PROCESSED_TYPE, errorDto);
        }
    }

    /**
     * Метод для назначения административного статуса точке доступа (up\down)
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
            return this.manageAdminStatus(domainName, interfaceName, propertyStatus.isEnabled());
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
     * Метод выполняет непосредственное обращение к rci API Keenetic OS для получения административного статуса точки
     * доступа. Не обрабатывает исключения
     *
     * @param domainName    FQDN роутера
     * @param interfaceName специфичное для производителя наименование точки доступа в API роутера
     * @return статус точки доступа, если его удалось получить (устройство доступно, реквизиты подходят и т.д.)
     */
    private AccessPointStatusResponse getAdminStatusRci(String domainName, String interfaceName) {
        AccessPointStatusRequest requestBody = new AccessPointStatusRequest(interfaceName);
        RequestEntity<AccessPointStatusRequest> request = new RequestEntity<>(requestBody, null,
                HttpMethod.POST, KeeneticRciUtils.buildInterfaceInformationUrl(domainName));
        ResponseEntity<AccessPointStatusResponse> response = this.restTemplate.exchange(request,
                AccessPointStatusResponse.class);
        return response.getBody();
    }

    /**
     * Метод для выполнения изменения административного статуса точки доступа только при необходимости:
     * предварительно проверяет текущий статус свойства и в случае, если он соответствует требуемому, не делает никаких
     * изменений
     *
     * @param domainName    FQDN роутера
     * @param interfaceName специфичное для производителя наименование точки доступа в API роутера
     * @param enabled       должно ли быть включено свойство
     * @return статус операции, если её удалось корректно завершить (устройство доступно, реквизиты подходят и т.д.)
     */
    private PropertyChangeStatusResponseDto manageAdminStatus(String domainName, String interfaceName,
                                                              boolean enabled) {
        AccessPointStatusResponse statusBefore = this.getAdminStatusRci(domainName, interfaceName);
        PropertyStatusResponseDto statusBeforeDto = this.propertyStatusMapper
                .mapToPropertyStatusResponseDto(statusBefore);
        if (statusBeforeDto.getError() != null) {
            return new PropertyChangeStatusResponseDto(PROCESSED_TYPE, statusBeforeDto.getError());
        } else if (statusBeforeDto.getEnabled().equals(enabled)) {
            return new PropertyChangeStatusResponseDto(PROCESSED_TYPE, false);
        } else {
            AccessPointStatusConfigResponse statusResult = this.setAdminStatusRci(domainName,
                    interfaceName,
                    enabled);
            return this.propertyStatusMapper.mapToPropertyChangeStatusResponseDto(statusResult);
        }
    }

    /**
     * Метод выполняет непосредственное обращение к rci API Keenetic OS для назначения административного статуса точке
     * доступа. Не обрабатывает исключения
     *
     * @param domainName    FQDN роутера
     * @param interfaceName специфичное для производителя наименование точки доступа в API роутера
     * @param enabled       должно ли быть включено свойство
     * @return статус точки доступа, если его удалось получить (устройство доступно, реквизиты подходят и т.д.)
     */
    private AccessPointStatusConfigResponse setAdminStatusRci(String domainName, String interfaceName,
                                                              boolean enabled) {
        AccessPointStatusConfigRequest requestBody = new AccessPointStatusConfigRequest(interfaceName, enabled);
        RequestEntity<AccessPointStatusConfigRequest> request = new RequestEntity<>(requestBody, null,
                HttpMethod.POST, KeeneticRciUtils.buildInterfaceManagementUrl(domainName));
        ResponseEntity<AccessPointStatusConfigResponse> response = this.restTemplate.exchange(request,
                AccessPointStatusConfigResponse.class);
        return response.getBody();
    }
}
