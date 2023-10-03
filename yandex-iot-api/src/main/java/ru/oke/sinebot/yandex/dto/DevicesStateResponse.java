package ru.oke.sinebot.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class DevicesStateResponse {
    @JsonProperty("request_id")
    private String requestId;

    private DevicesStatePayload payload;
}
