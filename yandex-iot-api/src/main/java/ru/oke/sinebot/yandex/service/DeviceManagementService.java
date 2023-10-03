package ru.oke.sinebot.yandex.service;

import ru.oke.sinebot.yandex.dto.DevicesActionRequest;
import ru.oke.sinebot.yandex.dto.DevicesActionResponsePayload;
import ru.oke.sinebot.yandex.dto.DevicesInfoPayload;
import ru.oke.sinebot.yandex.dto.DevicesStatePayload;
import ru.oke.sinebot.yandex.dto.DevicesStateRequest;

public interface DeviceManagementService {
    DevicesInfoPayload findUserDevices();

    DevicesStatePayload queryDevicesState(DevicesStateRequest request);

    DevicesActionResponsePayload performActionOnDevices(DevicesActionRequest request);
}
