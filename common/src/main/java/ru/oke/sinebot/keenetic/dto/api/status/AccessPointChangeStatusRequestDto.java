package ru.oke.sinebot.keenetic.dto.api.status;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.keenetic.dto.types.AccessPointType;
import ru.oke.sinebot.keenetic.dto.types.WifiFrequency;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessPointChangeStatusRequestDto {
    @NotNull
    private AccessPointType type;

    @NotNull
    private WifiFrequency band;

    @NotEmpty
    private List<PropertyChangeStatusRequestDto> properties;
}
