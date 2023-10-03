package ru.oke.sinebot.keenetic.service;

import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionRequestDto;
import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceRequestDto;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceStatusResponseDto;

import java.util.List;

public interface DeviceService {
    DeviceResponseDto create(DeviceRequestDto deviceRequestDto);

    void update(DeviceRequestDto deviceRequestDto);

    DeviceResponseDto findById(Long id);

    List<DeviceResponseDto> findByUserId(Long userId);

    void deleteById(Long id);

    DeviceStatusResponseDto queryDeviceAccessPointsStatus(Long id);

    DeviceChangeStatusResponseDto changeDeviceAccessPointsStatus(DeviceChangeStatusRequestDto changStatusDto);

    DeviceActionResponseDto executeAction(DeviceActionRequestDto actionRequestDto);

}
