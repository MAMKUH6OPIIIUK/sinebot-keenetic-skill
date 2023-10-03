package ru.oke.sinebot.keenetic.dto.api.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.keenetic.validator.UniqueAccessPoints;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceChangeStatusRequestDto {
    private Long id;

    @Valid
    @NotEmpty
    @UniqueAccessPoints
    private List<AccessPointChangeStatusRequestDto> accessPoints;
}
