package ru.oke.sinebot.keenetic.dto.api.status;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DeviceChangeStatusResponseDto {
    private Long id;

    private List<AccessPointChangeStatusResponseDto> accessPoints;
}
