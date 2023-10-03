package ru.oke.sinebot.keenetic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionRequestDto;
import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceRequestDto;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceStatusResponseDto;
import ru.oke.sinebot.keenetic.service.DevicePermissionService;
import ru.oke.sinebot.keenetic.service.DeviceService;
import ru.oke.sinebot.keenetic.service.TokenService;
import ru.oke.sinebot.oauth.config.CustomClaimConstants;

import java.util.List;

@SuppressWarnings("unused")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/device")
public class DeviceController {
    private final DeviceService deviceService;

    private final TokenService tokenService;

    private final DevicePermissionService permissionService;

    @GetMapping(value = "/{id}")
    public DeviceResponseDto findById(@PathVariable("id") Long id) {
        return this.deviceService.findById(id);
    }

    @GetMapping
    public List<DeviceResponseDto> findByUserId() {
        Long userId = (Long) this.tokenService.getTokenClaimValue(CustomClaimConstants.KEY_USER_ID);
        return this.deviceService.findByUserId(userId);
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public DeviceResponseDto create(@Validated @RequestBody DeviceRequestDto device) {
        Long userId = (Long) this.tokenService.getTokenClaimValue(CustomClaimConstants.KEY_USER_ID);
        device.setUserId(userId);
        DeviceResponseDto createdDevice = this.deviceService.create(device);
        this.permissionService.grantPermissions(createdDevice.getId());
        return createdDevice;
    }

    @PutMapping(value = "/{id}")
    public void update(@PathVariable("id") Long id, @Validated @RequestBody DeviceRequestDto device) {
        Long userId = (Long) this.tokenService.getTokenClaimValue(CustomClaimConstants.KEY_USER_ID);
        device.setId(id);
        device.setUserId(userId);
        this.deviceService.update(device);
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        this.deviceService.deleteById(id);
        this.permissionService.deletePermissions(id);
    }

    @GetMapping("/{id}/access-point/status")
    public DeviceStatusResponseDto queryDeviceStatus(@PathVariable("id") Long id) {
        return this.deviceService.queryDeviceAccessPointsStatus(id);
    }

    @PutMapping("/{id}/access-point/status")
    public DeviceChangeStatusResponseDto changeDeviceStatus(@PathVariable("id") Long id,
                                                            @Validated @RequestBody
                                                                    DeviceChangeStatusRequestDto requestDto) {
        requestDto.setId(id);
        return this.deviceService.changeDeviceAccessPointsStatus(requestDto);
    }

    @PostMapping("/{id}/action")
    public DeviceActionResponseDto executeAction(@PathVariable("id") Long deviceId,
                                                 @Validated @RequestBody DeviceActionRequestDto actionRequestDto) {
        actionRequestDto.setId(deviceId);
        return this.deviceService.executeAction(actionRequestDto);
    }
}
