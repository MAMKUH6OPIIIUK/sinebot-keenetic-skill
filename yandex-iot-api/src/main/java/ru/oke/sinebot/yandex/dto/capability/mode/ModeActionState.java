package ru.oke.sinebot.yandex.dto.capability.mode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;
import ru.oke.sinebot.yandex.dto.ActionResult;
import ru.oke.sinebot.yandex.dto.capability.ActionState;

@Data
@AllArgsConstructor
public class ModeActionState implements ActionState {
    private ModeInstance instance;

    @JsonProperty(value = "action_result")
    private ActionResult actionResult;

    public ModeActionState(ModeInstance instance) {
        this.instance = instance;
        this.actionResult = new ActionResult();
    }

    public ModeActionState(ModeInstance instance, ErrorCode errorCode, String errorMessage) {
        this.instance = instance;
        this.actionResult = new ActionResult(errorCode, errorMessage);
    }
}
