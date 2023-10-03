package ru.oke.sinebot.yandex.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.oke.sinebot.yandex.dto.DevicesActionRequest;
import ru.oke.sinebot.yandex.dto.DevicesActionResponse;
import ru.oke.sinebot.yandex.dto.DevicesActionResponsePayload;
import ru.oke.sinebot.yandex.dto.DevicesInfoPayload;
import ru.oke.sinebot.yandex.dto.DevicesInfoResponse;
import ru.oke.sinebot.yandex.dto.DevicesStatePayload;
import ru.oke.sinebot.yandex.dto.DevicesStateRequest;
import ru.oke.sinebot.yandex.dto.DevicesStateResponse;
import ru.oke.sinebot.yandex.service.DeviceManagementService;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/yandex/v1.0")
public class YandexController {

    private final DeviceManagementService deviceManagementService;

    @RequestMapping(method = RequestMethod.HEAD)
    public void isAlive() {

    }

    @GetMapping("/user/devices")
    public DevicesInfoResponse getUserDevices(@RequestHeader("X-Request-Id") String requestId) {
        DevicesInfoPayload payload = this.deviceManagementService.findUserDevices();
        return new DevicesInfoResponse(requestId, payload);
    }

    @PostMapping("/user/devices/query")
    public DevicesStateResponse queryDevices(@RequestHeader("X-Request-Id") String requestId,
                                             @RequestBody DevicesStateRequest request) {
        DevicesStatePayload payload = this.deviceManagementService.queryDevicesState(request);
        return new DevicesStateResponse(requestId, payload);
    }

    @PostMapping("/user/devices/action")
    public DevicesActionResponse performAction(@RequestHeader("X-Request-Id") String requestId,
                                               @RequestBody DevicesActionRequest request) {
        DevicesActionResponsePayload payload = this.deviceManagementService.performActionOnDevices(request);
        return new DevicesActionResponse(requestId, payload);
    }

}
