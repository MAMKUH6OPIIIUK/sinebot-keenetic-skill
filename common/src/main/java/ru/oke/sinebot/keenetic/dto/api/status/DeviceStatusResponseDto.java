package ru.oke.sinebot.keenetic.dto.api.status;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DeviceStatusResponseDto {
    private Long id;

    private List<AccessPointStatusResponseDto> accessPoints;
}
