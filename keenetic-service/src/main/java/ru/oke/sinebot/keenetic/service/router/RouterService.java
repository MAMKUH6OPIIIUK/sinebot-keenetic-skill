package ru.oke.sinebot.keenetic.service.router;

import ru.oke.sinebot.keenetic.dto.api.action.ActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointChangeStatusDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointStatusDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyStatusResponseDto;

import java.util.List;

/**
 * Интерфейс для всех сервисов, непосредственно взаимодействующих с роутерами различных производителей. Предоставляет
 * абстрагированный от конкретного производителя набор методов для получения информации о состоянии роутера или
 * изменения его состояния
 *
 * @author k.oshoev
 */
public interface RouterService {
    String getSupportedVendorName();

    List<PropertyStatusResponseDto> getAccessPointStatus(AccessPointStatusDto accessPoint);

    List<PropertyChangeStatusResponseDto> setAccessPointStatus(AccessPointChangeStatusDto accessPoint);

    ActionResponseDto executeAction(DeviceActionRequestDto requestDto);
}
