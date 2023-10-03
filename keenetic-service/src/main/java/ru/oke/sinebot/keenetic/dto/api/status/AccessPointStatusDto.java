package ru.oke.sinebot.keenetic.dto.api.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Класс описывает DTO  для внутреннего взаимодействия между сервисами в целях запроса информации о статусах свойств
 * конкретной точки доступа определенного устройства
 *
 * @author k.oshoev
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessPointStatusDto {
    private String domainName;

    private String interfaceName;

    private List<PropertyStatusRequestDto> properties;
}
