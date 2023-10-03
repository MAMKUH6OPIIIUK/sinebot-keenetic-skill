package ru.oke.sinebot.yandex.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.api.action.ActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionRequestDto;
import ru.oke.sinebot.keenetic.dto.api.action.DeviceActionResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.AccessPointResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.ActionDto;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.DeviceStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.yandex.dto.ActionStatus;
import ru.oke.sinebot.yandex.dto.Device;
import ru.oke.sinebot.yandex.dto.DeviceActionRequest;
import ru.oke.sinebot.yandex.dto.DeviceActionResponse;
import ru.oke.sinebot.yandex.dto.DeviceInfo;
import ru.oke.sinebot.yandex.dto.DeviceState;
import ru.oke.sinebot.yandex.dto.capability.Capability;
import ru.oke.sinebot.yandex.dto.capability.CapabilityActionState;
import ru.oke.sinebot.yandex.dto.capability.CapabilityState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DeviceMapper {
    private final CapabilityMapper capabilityMapper;

    private final DeviceInfoMapper deviceInfoMapper;


    public Device mapToDevice(DeviceResponseDto deviceDto) {
        Device device = new Device();
        device.setId(deviceDto.getId().toString());
        device.setName(deviceDto.getName());
        device.setDescription(deviceDto.getDescription());
        DeviceInfo deviceInfo = this.deviceInfoMapper.mapToDeviceInfo(deviceDto.getDeviceInfo());
        device.setDeviceInfo(deviceInfo);
        List<Capability> modeCapabilities = this.capabilityMapper.mapToModeCapabilities(deviceDto.getAccessPoints());
        Capability onOffCapability = this.capabilityMapper.mapToOnOffCapability(deviceDto.getSupportedActions());
        List<Capability> deviceCapabilities = new ArrayList<>();
        deviceCapabilities.addAll(modeCapabilities);
        if (onOffCapability != null) {
            deviceCapabilities.add(onOffCapability);
        }
        device.setCapabilities(deviceCapabilities);
        return device;
    }

    public DeviceState mapToDeviceState(DeviceStatusResponseDto deviceDto) {
        List<ErrorDto> errorsHolder = new ArrayList<>();
        List<CapabilityState> capabilities = this.capabilityMapper.mapToCapabilityStates(deviceDto.getAccessPoints(),
                errorsHolder);
        String deviceId = deviceDto.getId().toString();
        if (!errorsHolder.isEmpty()) {
            ErrorCode errorCode = errorsHolder.get(0).getErrorCode();
            String errorMessage = errorsHolder.stream()
                    .map(ErrorDto::getErrorMessage)
                    .collect(Collectors.joining("; "));
            return new DeviceState(deviceId, errorCode, errorMessage);
        }
        return new DeviceState(deviceId, capabilities);
    }

    public DeviceChangeStatusRequestDto mapToDeviceChangeStatus(DeviceActionRequest deviceRequest,
                                                                DeviceResponseDto deviceInfo) {
        List<CapabilityState> requestedStates = deviceRequest.getCapabilities();
        List<AccessPointResponseDto> knownAccessPoints = deviceInfo.getAccessPoints();
        List<AccessPointChangeStatusRequestDto> accessPoints = this.capabilityMapper.mapToAccessPoints(requestedStates,
                knownAccessPoints);
        if (accessPoints.isEmpty()) {
            return null;
        }
        DeviceChangeStatusRequestDto result = new DeviceChangeStatusRequestDto();
        result.setAccessPoints(accessPoints);
        return result;
    }

    public DeviceActionRequestDto mapToDeviceAction(DeviceActionRequest deviceRequest) {
        ActionDto onOffAction = this.capabilityMapper.mapToActionDto(deviceRequest.getCapabilities());
        if (onOffAction == null) {
            return null;
        }
        DeviceActionRequestDto result = new DeviceActionRequestDto();
        result.setAction(onOffAction);
        return result;
    }

    public DeviceActionResponse mapToDeviceActionResponse(DeviceActionResponseDto deviceResponse) {
        ActionResponseDto actionState = deviceResponse.getActionResult();
        List<CapabilityActionState> capabilityState = this.capabilityMapper.mapToCapabilityStates(actionState);
        return new DeviceActionResponse(deviceResponse.getId().toString(), capabilityState);
    }

    public DeviceActionResponse mapToDeviceActionResponse(DeviceChangeStatusResponseDto deviceResponse) {
        List<AccessPointChangeStatusResponseDto> accessPoints = deviceResponse.getAccessPoints();
        List<CapabilityActionState> capabilityState = this.capabilityMapper.mapToCapabilityStates(accessPoints);
        return new DeviceActionResponse(deviceResponse.getId().toString(), capabilityState);
    }

    public DeviceActionResponse mergeActionResponses(String deviceId, DeviceActionResponse response1,
                                                     DeviceActionResponse response2) {
        if (response1 == null && response2 == null) {
            return new DeviceActionResponse(deviceId, ErrorCode.INVALID_ACTION,
                    "Действия не были выполнены");
        } else if (response1 == null) {
            return response2;
        } else if (response2 == null) {
            return response1;
        }
        List<CapabilityActionState> mergedStates = this.capabilityMapper.mergeCapabilities(response1.getCapabilities(),
                response2.getCapabilities());
        boolean existsSuccess = mergedStates.stream()
                .anyMatch(capability -> capability.getState().getActionResult().getStatus() == ActionStatus.DONE);
        if (existsSuccess) {
            return new DeviceActionResponse(deviceId, mergedStates);
        } else {
            return new DeviceActionResponse(deviceId, ErrorCode.INVALID_ACTION,
                    "Действия не были выполнены");
        }
    }
}
