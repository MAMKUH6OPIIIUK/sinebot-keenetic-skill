package ru.oke.sinebot.yandex.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;
import ru.oke.sinebot.yandex.dto.capability.CapabilityActionState;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceActionResponse {
    private String id;

    private List<CapabilityActionState> capabilities;

    @JsonProperty(value = "action_result")
    private ActionResult actionResult;

    public DeviceActionResponse(String id, List<CapabilityActionState> capabilities) {
        this.id = id;
        this.capabilities = capabilities;
    }

    public DeviceActionResponse(String id, ActionResult actionResult) {
        this.id = id;
        this.actionResult = actionResult;
    }

    public DeviceActionResponse(String id, ErrorCode errorCode, String errorMessage) {
        this.id = id;
        this.actionResult = new ActionResult(errorCode, errorMessage);
    }
}
