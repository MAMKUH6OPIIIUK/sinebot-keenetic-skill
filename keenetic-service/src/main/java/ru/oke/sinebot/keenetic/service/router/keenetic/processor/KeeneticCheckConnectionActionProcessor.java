package ru.oke.sinebot.keenetic.service.router.keenetic.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import ru.oke.sinebot.keenetic.dto.api.action.ActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.keenetic.dto.types.ActionType;
import ru.oke.sinebot.keenetic.exception.NotFoundException;
import ru.oke.sinebot.keenetic.service.router.keenetic.CachingKeeneticAuthService;
import ru.oke.sinebot.keenetic.service.router.keenetic.KeeneticRciUtils;


/**
 * Обработчик для выполнения действий типа CHECK_CONNECTION. Позволяет выполнить проверку возможности подключиться к
 * устройству
 *
 * @author k.oshoev
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeeneticCheckConnectionActionProcessor implements KeeneticActionProcessor {
    private static final ActionType PROCESSED_TYPE = ActionType.CHECK_CONNECTION;

    private final CachingKeeneticAuthService authService;

    /**
     * Метод возвращает поддерживаемый данным процессором тип действия
     *
     * @return тип CHECK_CONNECTION (проверка подключения)
     */
    @Override
    public ActionType getProcessedActionType() {
        return PROCESSED_TYPE;
    }

    /**
     * Метод проверяет возможность подключиться и пройти аутентификацию на заданном роутере
     *
     * @param domainName FQDN роутера
     * @return успешный результат, если подключение выполнено успешно
     */
    @Override
    public ActionResponseDto executeAction(String domainName) {
        try {
            String sessionCookie = this.authService.getSessionCookie(domainName, KeeneticRciUtils.DEFAULT_PROTOCOL);
            if (sessionCookie == null) {
                ErrorDto error = new ErrorDto(ErrorCode.DEVICE_UNREACHABLE, "Сессия с устройством не " +
                        "была успешно установлена");
                return new ActionResponseDto(PROCESSED_TYPE, error);
            }
            return new ActionResponseDto(PROCESSED_TYPE);
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

}
