package ru.oke.sinebot.yandex.dto.capability.onoff;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;
import ru.oke.sinebot.yandex.dto.ActionResult;
import ru.oke.sinebot.yandex.dto.capability.ActionState;

@Data
public class OnOffActionState implements ActionState {
    private OnOffInstance instance;

    @JsonProperty(value = "action_result")
    private ActionResult actionResult;

    public OnOffActionState() {
        this.instance = OnOffInstance.ON;
        this.actionResult = new ActionResult();
    }

    public OnOffActionState(ErrorCode errorCode, String errorMessage) {
        this.instance = OnOffInstance.ON;
        this.actionResult = new ActionResult(errorCode, errorMessage);
    }
}
