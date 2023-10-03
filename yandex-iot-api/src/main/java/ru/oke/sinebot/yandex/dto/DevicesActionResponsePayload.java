package ru.oke.sinebot.yandex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class DevicesActionResponsePayload {
    private List<DeviceActionResponse> devices;
}
