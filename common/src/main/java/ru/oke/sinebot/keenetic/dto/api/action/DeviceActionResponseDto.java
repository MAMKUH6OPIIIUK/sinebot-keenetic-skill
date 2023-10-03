package ru.oke.sinebot.keenetic.dto.api.action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceActionResponseDto {
    private Long id;

    private ActionResponseDto actionResult;
}
