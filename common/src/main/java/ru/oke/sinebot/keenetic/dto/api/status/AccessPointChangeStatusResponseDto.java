package ru.oke.sinebot.keenetic.dto.api.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.keenetic.dto.types.AccessPointType;
import ru.oke.sinebot.keenetic.dto.types.WifiFrequency;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessPointChangeStatusResponseDto {
    private AccessPointType type;

    private WifiFrequency band;

    private List<PropertyChangeStatusResponseDto> properties;
}
