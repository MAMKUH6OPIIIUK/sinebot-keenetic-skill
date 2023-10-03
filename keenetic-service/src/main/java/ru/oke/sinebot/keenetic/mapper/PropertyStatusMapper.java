package ru.oke.sinebot.keenetic.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.rci.common.ActionEntityStatus;
import ru.oke.sinebot.keenetic.dto.rci.config.wifi.AccessPointStatusConfigResponse;
import ru.oke.sinebot.keenetic.dto.rci.config.wifi.AccessPointWpsConfigResponse;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.AccessPointStatusResponse;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.AccessPointWpsSessionResponse;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.AccessPointWpsStatusResponse;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.AdminState;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps.State;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps.WpsStatus;
import ru.oke.sinebot.keenetic.dto.types.PropertyType;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PropertyStatusMapper {
    private final ErrorMapper errorMapper;

    /**
     * Метод для маппинга ответа со статусом WPS от роутера Keenetic на внутренний объект со статусом свойства точки
     * доступа. Ответ от устройства может содержать либо ошибку получения статуса, либо сам корректно полученный статус
     *
     * @param wpsStatusResponse ответ от устройства, полученный на запрос статуса wps
     * @return dto со статусом для использования во внутреннем сервисе
     */
    public PropertyStatusResponseDto mapToPropertyStatusResponseDto(AccessPointWpsStatusResponse wpsStatusResponse) {
        WpsStatus wpsStatus = wpsStatusResponse.getWps().getWpsStatus();
        List<ActionEntityStatus> actionStatuses = wpsStatusResponse.getWps().getActionStatus();
        ErrorDto error = this.errorMapper.mapToErrorDto(actionStatuses);
        if (error != null) {
            return new PropertyStatusResponseDto(PropertyType.WPS, error);
        }
        boolean isWpsActive = wpsStatus.getWps().getStatus() == State.ACTIVE;
        return new PropertyStatusResponseDto(PropertyType.WPS, isWpsActive);
    }

    public PropertyStatusResponseDto mapToPropertyStatusResponseDto(AccessPointStatusResponse adminStatusResponse) {
        AdminState state = adminStatusResponse.getState();
        List<ActionEntityStatus> actionStatuses = adminStatusResponse.getActionStatus();
        ErrorDto error = this.errorMapper.mapToErrorDto(actionStatuses);
        if (error != null) {
            return new PropertyStatusResponseDto(PropertyType.WPS, error);
        }
        boolean isUp = state == AdminState.UP;
        return new PropertyStatusResponseDto(PropertyType.STATE, isUp);
    }

    public PropertyChangeStatusResponseDto mapToPropertyChangeStatusResponseDto(AccessPointStatusConfigResponse
                                                                                        configResponse) {
        List<ActionEntityStatus> actionStatuses = configResponse.getUp().getStatus();
        ErrorDto error = this.errorMapper.mapToErrorDto(actionStatuses);
        return this.mapToPropertyChangeStatusResponseDto(PropertyType.STATE, error);
    }

    public PropertyChangeStatusResponseDto mapToPropertyChangeStatusResponseDto(AccessPointWpsConfigResponse
                                                                                        configResponse) {
        List<ActionEntityStatus> actionStatuses = configResponse.getWps().getStatus();
        ErrorDto error = this.errorMapper.mapToErrorDto(actionStatuses);
        return this.mapToPropertyChangeStatusResponseDto(PropertyType.WPS, error);
    }

    public PropertyChangeStatusResponseDto mapToPropertyChangeStatusResponseDto(AccessPointWpsSessionResponse
                                                                                        sessionResponse) {
        List<ActionEntityStatus> actionStatuses = sessionResponse.getWps().getButton().getStatus();
        ErrorDto error = this.errorMapper.mapToErrorDto(actionStatuses);
        if (error != null) {
            return new PropertyChangeStatusResponseDto(PropertyType.WPS, error);
        }
        return new PropertyChangeStatusResponseDto(PropertyType.WPS, true, false);
    }

    private PropertyChangeStatusResponseDto mapToPropertyChangeStatusResponseDto(PropertyType type, ErrorDto errorDto) {
        if (errorDto != null) {
            return new PropertyChangeStatusResponseDto(type, errorDto);
        }
        return new PropertyChangeStatusResponseDto(type, true);
    }
}
