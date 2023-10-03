package ru.oke.sinebot.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DevicesInfoResponse {
    @JsonProperty("request_id")
    private String requestId;

    private DevicesInfoPayload payload;
}
