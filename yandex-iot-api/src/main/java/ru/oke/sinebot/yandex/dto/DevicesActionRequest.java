package ru.oke.sinebot.yandex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DevicesActionRequest {
    private DevicesActionRequestPayload payload;
}
