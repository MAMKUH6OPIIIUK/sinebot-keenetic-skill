package ru.oke.sinebot.keenetic.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.api.action.ActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.ActionDto;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.keenetic.dto.rci.common.ActionEntityStatus;
import ru.oke.sinebot.keenetic.dto.rci.config.system.ConfigurationSaveResponse;
import ru.oke.sinebot.keenetic.dto.rci.operational.system.SystemRebootResponse;
import ru.oke.sinebot.keenetic.dto.types.ActionType;
import ru.oke.sinebot.keenetic.model.action.Action;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ActionMapper {
    private final ErrorMapper errorMapper;

    public ActionDto mapToActionDto(Action action) {
        return new ActionDto(action.getType());
    }

    public ActionResponseDto mapToActionResponseDto(SystemRebootResponse rebootResponse) {
        List<ActionEntityStatus> actionStatus = rebootResponse.getReboot().getStatus();
        return this.mapToActionResponseDto(ActionType.RELOAD, actionStatus);
    }

    public ActionResponseDto mapToActionResponseDto(ConfigurationSaveResponse saveResponse) {
        List<ActionEntityStatus> actionStatus = saveResponse.getConfiguration().getSave().getStatus();
        return this.mapToActionResponseDto(ActionType.SAVE_CONFIG, actionStatus);
    }

    private ActionResponseDto mapToActionResponseDto(ActionType type, List<ActionEntityStatus> actionStatus) {
        ErrorDto error = this.errorMapper.mapToErrorDto(actionStatus);
        if (error != null) {
            return new ActionResponseDto(type, error);
        }
        return new ActionResponseDto(type);
    }
}
