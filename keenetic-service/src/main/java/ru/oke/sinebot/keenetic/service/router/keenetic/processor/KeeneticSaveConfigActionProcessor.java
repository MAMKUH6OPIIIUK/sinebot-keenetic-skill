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
import ru.oke.sinebot.keenetic.dto.api.action.ActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.keenetic.dto.rci.config.system.ConfigurationSaveRequest;
import ru.oke.sinebot.keenetic.dto.rci.config.system.ConfigurationSaveResponse;
import ru.oke.sinebot.keenetic.dto.types.ActionType;
import ru.oke.sinebot.keenetic.exception.NotFoundException;
import ru.oke.sinebot.keenetic.mapper.ActionMapper;
import ru.oke.sinebot.keenetic.service.router.keenetic.KeeneticAuthInterceptor;
import ru.oke.sinebot.keenetic.service.router.keenetic.KeeneticRciUtils;

/**
 * Обработчик для выполнения действий типа SAVE_CONFIG. Позволяет сохранить конфигурацию роутера.
 */
@Slf4j
@Service
public class KeeneticSaveConfigActionProcessor implements KeeneticActionProcessor {
    private static final ActionType PROCESSED_TYPE = ActionType.SAVE_CONFIG;

    private final RestTemplate restTemplate;

    private final ActionMapper actionMapper;

    /**
     * Конструктор для внедрения специально подготовленного RestTemplate, имеющего interceptor
     * {@link KeeneticAuthInterceptor} для выполнения аутентификации
     *
     * @param restTemplate подготовленный через конфигурацию bean RestTemplate
     * @param actionMapper маппер результатов выполнения действия. Используется для приведения ответов роутера к
     *                     объектам, пригодным для использования во внутреннем API приложения
     */
    public KeeneticSaveConfigActionProcessor(@Qualifier(RestConfig.KEENETIC_REST_TEMPLATE) RestTemplate restTemplate,
                                             ActionMapper actionMapper) {
        this.restTemplate = restTemplate;
        this.actionMapper = actionMapper;
    }

    /**
     * Метод возвращает поддерживаемый данным процессором тип действия
     *
     * @return тип SAVE_CONFIG (сохранение конфигурации)
     */
    @Override
    public ActionType getProcessedActionType() {
        return PROCESSED_TYPE;
    }

    /**
     * Метод для выполнения действия по сохранению конфигурации на заданном роутере. Выполняет обработку всех исключений
     *
     * @param domainName FQDN роутера
     * @return результат выполнения действия
     */
    @Override
    public ActionResponseDto executeAction(String domainName) {
        try {
            ConfigurationSaveResponse response = this.saveConfigurationRci(domainName);
            return this.actionMapper.mapToActionResponseDto(response);
        } catch (HttpClientErrorException | ResourceAccessException e) {
            log.error("Ошибка подключения к устройству \"{}\"", domainName, e);
            ErrorDto errorDto = new ErrorDto(ErrorCode.DEVICE_UNREACHABLE, e.getMessage());
            return new ActionResponseDto(PROCESSED_TYPE, errorDto);
        } catch (NotFoundException e) {
            log.error("Устройство с именем \"{}\" не найдено: {}", domainName, e.getMessage());
            ErrorDto errorDto = new ErrorDto(ErrorCode.DEVICE_NOT_FOUND, e.getMessage());
            return new ActionResponseDto(PROCESSED_TYPE, errorDto);
        } catch (Throwable e) {
            log.error("Неожиданная ошибка при взаимодействии с устройством \"{}\"", domainName, e);
            ErrorDto errorDto = new ErrorDto(ErrorCode.INTERNAL_ERROR, e.getMessage());
            return new ActionResponseDto(PROCESSED_TYPE, errorDto);
        }
    }

    /**
     * Метод выполняет непосредственное обращение к rci API роутера Keenetic для сохранения конфигурации. В результате
     * выполнения running-config копируется в startup-config
     *
     * @param domainName FQDN роутера
     * @return ответ роутера на запрос сохранения конфигурации
     */
    private ConfigurationSaveResponse saveConfigurationRci(String domainName) {
        ConfigurationSaveRequest requestBody = new ConfigurationSaveRequest();
        RequestEntity<ConfigurationSaveRequest> request = new RequestEntity<>(requestBody, null, HttpMethod.POST,
                KeeneticRciUtils.buildSystemUri(domainName));
        ResponseEntity<ConfigurationSaveResponse> response = this.restTemplate.exchange(request,
                ConfigurationSaveResponse.class);
        return response.getBody();
    }
}
