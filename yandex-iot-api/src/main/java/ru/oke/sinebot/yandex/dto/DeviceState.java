package ru.oke.sinebot.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;
import ru.oke.sinebot.yandex.dto.capability.CapabilityState;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class DeviceState {
    private String id;

    private List<CapabilityState> capabilities;

    @JsonProperty("error_code")
    private ErrorCode errorCode;

    @JsonProperty("error_message")
    private String errorMessage;

    public DeviceState(String id, List<CapabilityState> capabilities) {
        this.id = id;
        this.capabilities = capabilities;
    }

    public DeviceState(String id, ErrorCode errorCode, String errorMessage) {
        this.id = id;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
