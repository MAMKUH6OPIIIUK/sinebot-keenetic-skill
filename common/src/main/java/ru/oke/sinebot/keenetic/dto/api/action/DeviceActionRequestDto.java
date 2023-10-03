package ru.oke.sinebot.keenetic.dto.api.action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.keenetic.dto.api.info.ActionDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceActionRequestDto {
    private Long id;

    private String domainName;

    private ActionDto action;
}
