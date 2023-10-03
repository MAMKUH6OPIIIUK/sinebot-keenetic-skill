package ru.oke.sinebot.keenetic.dto.api.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO с базовой информацией о наименованиях производителя и модели устройства
 *
 * @author k.oshoev
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfoDto {
    private Long modelId;

    private String vendor;

    private String model;
}
