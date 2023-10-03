package ru.oke.sinebot.yandex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DevicesActionRequestPayload {
    private List<DeviceActionRequest> devices;
}
