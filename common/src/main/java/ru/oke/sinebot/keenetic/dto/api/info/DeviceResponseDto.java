package ru.oke.sinebot.keenetic.dto.api.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceResponseDto {
    private Long id;

    private Long userId;

    private String name;

    private String description;

    private String domainName;

    private DeviceInfoDto deviceInfo;

    private List<AccessPointResponseDto> accessPoints;

    private List<ActionDto> supportedActions;
}
