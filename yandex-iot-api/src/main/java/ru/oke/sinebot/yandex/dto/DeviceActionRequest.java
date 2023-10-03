package ru.oke.sinebot.yandex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.yandex.dto.capability.CapabilityState;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceActionRequest {
    private String id;

    private List<CapabilityState> capabilities;
}
