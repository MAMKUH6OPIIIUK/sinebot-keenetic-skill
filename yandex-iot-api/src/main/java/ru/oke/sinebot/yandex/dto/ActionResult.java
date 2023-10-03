package ru.oke.sinebot.yandex.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionResult {
    private ActionStatus status;

    @JsonProperty(value = "error_code")
    private ErrorCode errorCode;

    @JsonProperty(value = "error_message")
    private String errorMessage;

    public ActionResult() {
        this.status = ActionStatus.DONE;
    }

    public ActionResult(ErrorCode errorCode, String errorMessage) {
        this.status = ActionStatus.ERROR;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
