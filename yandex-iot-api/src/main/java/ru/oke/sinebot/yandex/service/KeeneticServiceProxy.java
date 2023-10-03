package ru.oke.sinebot.yandex.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionRequestDto;
import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceStatusResponseDto;

import java.util.List;

@FeignClient(name = "sinebot-keenetic-service", path = "/api/device")
public interface KeeneticServiceProxy {
    @GetMapping(value = "/{id}")
    DeviceResponseDto findById(@PathVariable("id") Long id);

    @GetMapping
    List<DeviceResponseDto> findByUserId();

    @GetMapping("/{id}/access-point/status")
    DeviceStatusResponseDto queryDeviceStatus(@PathVariable("id") Long id);

    @PutMapping("/{id}/access-point/status")
    DeviceChangeStatusResponseDto changeDeviceStatus(@PathVariable("id") Long id,
                                                            @RequestBody DeviceChangeStatusRequestDto requestDto);

    @PostMapping("/{id}/action")
    DeviceActionResponseDto executeAction(@PathVariable("id") Long deviceId,
                                          @RequestBody DeviceActionRequestDto actionRequestDto);
}
