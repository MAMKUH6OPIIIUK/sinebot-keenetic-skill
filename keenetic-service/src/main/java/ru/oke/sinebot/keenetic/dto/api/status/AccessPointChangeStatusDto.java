package ru.oke.sinebot.keenetic.dto.api.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.keenetic.dto.types.AccessPointType;
import ru.oke.sinebot.keenetic.dto.types.WifiFrequency;

import java.util.List;

/**
 * Класс описывает DTO  для внутреннего взаимодействия между сервисами в целях назначения статусов свойствам конкретной
 * точки доступа определенного устройства
 *
 * @author k.oshoev
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessPointChangeStatusDto {
    private String domainName;

    private AccessPointType type;

    private WifiFrequency band;

    private String interfaceName;

    private List<PropertyChangeStatusRequestDto> properties;
}
