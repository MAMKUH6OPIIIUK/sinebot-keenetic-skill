package ru.oke.sinebot.keenetic.dto.api.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.keenetic.dto.types.AccessPointType;
import ru.oke.sinebot.keenetic.dto.types.WifiFrequency;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessPointResponseDto {
    private AccessPointType type;

    private WifiFrequency band;

    private String interfaceName;

    private List<PropertyResponseDto> properties;
}
