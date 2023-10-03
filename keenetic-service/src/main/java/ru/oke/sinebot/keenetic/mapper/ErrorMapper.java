package ru.oke.sinebot.keenetic.mapper;

import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.keenetic.dto.rci.common.ActionEntityStatus;
import ru.oke.sinebot.keenetic.dto.rci.common.ResultStatus;

import java.util.List;

@Component
public class ErrorMapper {
    public ErrorDto mapToErrorDto(List<ActionEntityStatus> actionStatuses) {
        if (actionStatuses != null) {
            for (ActionEntityStatus actionStatus : actionStatuses) {
                if (actionStatus.getStatus() == ResultStatus.ERROR) {
                    return new ErrorDto(ErrorCode.INVALID_ACTION,
                            "Получена ошибка от устройства: " + actionStatus.getMessage());
                }
            }
        }
        return null;
    }
}
