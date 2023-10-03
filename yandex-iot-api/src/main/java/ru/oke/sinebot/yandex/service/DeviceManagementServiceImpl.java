package ru.oke.sinebot.yandex.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionRequestDto;
import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.oauth.config.CustomClaimConstants;
import ru.oke.sinebot.yandex.dto.Device;
import ru.oke.sinebot.yandex.dto.DeviceActionRequest;
import ru.oke.sinebot.yandex.dto.DeviceActionResponse;
import ru.oke.sinebot.yandex.dto.DeviceId;
import ru.oke.sinebot.yandex.dto.DeviceState;
import ru.oke.sinebot.yandex.dto.DevicesActionRequest;
import ru.oke.sinebot.yandex.dto.DevicesActionResponsePayload;
import ru.oke.sinebot.yandex.dto.DevicesInfoPayload;
import ru.oke.sinebot.yandex.dto.DevicesStatePayload;
import ru.oke.sinebot.yandex.dto.DevicesStateRequest;
import ru.oke.sinebot.yandex.exception.KeeneticServiceException;
import ru.oke.sinebot.yandex.mapper.DeviceMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceManagementServiceImpl implements DeviceManagementService {
    private final KeeneticServiceProxy keeneticService;

    private final TokenService tokenService;

    private final DeviceMapper deviceMapper;

    @Override
    public DevicesInfoPayload findUserDevices() {
        List<DeviceResponseDto> userDevices = keeneticService.findByUserId();
        Long userId = (Long) tokenService.getTokenClaimValue(CustomClaimConstants.KEY_USER_ID);
        List<Device> devices = userDevices.stream()
                .map(deviceMapper::mapToDevice)
                .collect(Collectors.toList());
        return new DevicesInfoPayload(userId.toString(), devices);
    }

    @Override
    public DevicesStatePayload queryDevicesState(DevicesStateRequest request) {
        List<DeviceState> deviceStates = new ArrayList<>();
        for (DeviceId deviceId : request.getDevices()) {
            deviceStates.add(this.queryDeviceState(deviceId.getId()));
        }
        return new DevicesStatePayload(deviceStates);
    }

    @Override
    public DevicesActionResponsePayload performActionOnDevices(DevicesActionRequest request) {
        List<DeviceActionResponse> devicesActionsStatus = new ArrayList<>();
        for (DeviceActionRequest actionRequest : request.getPayload().getDevices()) {
            DeviceActionResponse response = this.performAction(actionRequest);
            devicesActionsStatus.add(response);
        }
        return new DevicesActionResponsePayload(devicesActionsStatus);
    }

    private DeviceState queryDeviceState(String id) {
        try {
            DeviceStatusResponseDto deviceResponse = keeneticService.queryDeviceStatus(Long.valueOf(id));
            return this.deviceMapper.mapToDeviceState(deviceResponse);
        } catch (KeeneticServiceException e) {
            ErrorDto error = e.getError();
            log.error(error.toString());
            return new DeviceState(id, error.getErrorCode(), error.getErrorMessage());
        } catch (Exception e) {
            log.error(e.getClass() + e.getMessage());
            return new DeviceState(id, ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    private DeviceActionResponse performAction(DeviceActionRequest request) {
        try {
            Long deviceId = Long.valueOf(request.getId());
            DeviceResponseDto deviceInfo = keeneticService.findById(deviceId);
            DeviceActionResponse changeStateResponse = this.executeRouterChangeStatus(request, deviceInfo);
            DeviceActionResponse actionResponse = this.executeActionOnRouter(request);
            return this.deviceMapper.mergeActionResponses(request.getId(), changeStateResponse, actionResponse);
        } catch (KeeneticServiceException e) {
            ErrorDto error = e.getError();
            log.error(error.toString());
            return new DeviceActionResponse(request.getId(), error.getErrorCode(), error.getErrorMessage());
        } catch (Exception e) {
            log.error(e.getClass() + e.getMessage());
            return new DeviceActionResponse(request.getId(), ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    private DeviceActionResponse executeRouterChangeStatus(DeviceActionRequest request, DeviceResponseDto deviceInfo) {
        Long deviceId = Long.valueOf(request.getId());
        DeviceChangeStatusRequestDto statusRequest = this.deviceMapper.mapToDeviceChangeStatus(request, deviceInfo);
        if (statusRequest != null) {
            try {
                DeviceChangeStatusResponseDto responseDto = this.keeneticService.changeDeviceStatus(deviceId,
                        statusRequest);
                return this.deviceMapper.mapToDeviceActionResponse(responseDto);
            } catch (KeeneticServiceException e) {
                ErrorDto error = e.getError();
                log.error(error.toString());
                return new DeviceActionResponse(request.getId(), error.getErrorCode(), error.getErrorMessage());
            } catch (Exception e) {
                log.error(e.getClass() + e.getMessage());
                return new DeviceActionResponse(request.getId(), ErrorCode.INTERNAL_ERROR, e.getMessage());
            }
        }
        return null;
    }

    private DeviceActionResponse executeActionOnRouter(DeviceActionRequest request) {
        Long deviceId = Long.valueOf(request.getId());
        DeviceActionRequestDto actionRequest = this.deviceMapper.mapToDeviceAction(request);
        if (actionRequest != null) {
            try {
                DeviceActionResponseDto responseDto = this.keeneticService.executeAction(deviceId, actionRequest);
                return this.deviceMapper.mapToDeviceActionResponse(responseDto);
            } catch (KeeneticServiceException e) {
                ErrorDto error = e.getError();
                log.error(error.toString());
                return new DeviceActionResponse(request.getId(), error.getErrorCode(), error.getErrorMessage());
            } catch (Exception e) {
                log.error(e.getClass() + e.getMessage());
                return new DeviceActionResponse(request.getId(), ErrorCode.INTERNAL_ERROR, e.getMessage());
            }
        }
        return null;
    }
}
