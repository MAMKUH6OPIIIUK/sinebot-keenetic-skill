package ru.oke.sinebot.keenetic.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.api.info.AccessPointResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.ActionDto;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceInfoDto;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceRequestDto;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceResponseDto;
import ru.oke.sinebot.keenetic.model.Device;
import ru.oke.sinebot.keenetic.model.Model;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DeviceMapper {
    private final ModelMapper modelMapper;

    private final AccessPointMapper accessPointMapper;

    private final ActionMapper actionMapper;

    public DeviceResponseDto mapToDeviceResponseDto(Device device) {
        DeviceResponseDto dto = new DeviceResponseDto();
        dto.setId(device.getId());
        dto.setUserId(device.getUserId());
        dto.setName(device.getName());
        dto.setDescription(device.getDescription());
        dto.setDomainName(device.getDomainName());
        DeviceInfoDto deviceModelInfo = modelMapper.mapToDeviceInfoDto(device.getModel());
        dto.setDeviceInfo(deviceModelInfo);
        List<AccessPointResponseDto> accessPointDtos = device.getModel().getAccessPoints().stream()
                .map(accessPointMapper::mapToResponseDto)
                .collect(Collectors.toList());
        dto.setAccessPoints(accessPointDtos);
        List<ActionDto> actionDtos = device.getModel().getSupportedActions().stream()
                .map(actionMapper::mapToActionDto)
                .collect(Collectors.toList());
        dto.setSupportedActions(actionDtos);
        return dto;
    }

    public Device mapToDevice(DeviceRequestDto deviceRequestDto, Model model) {
        Device device = new Device();
        device.setId(deviceRequestDto.getId());
        device.setUserId(deviceRequestDto.getUserId());
        device.setModel(model);
        device.setName(deviceRequestDto.getName());
        device.setDescription(deviceRequestDto.getDescription());
        device.setDomainName(deviceRequestDto.getDomainName());
        device.setLogin(deviceRequestDto.getLogin());
        device.setPassword(deviceRequestDto.getPassword());
        return device;
    }

    public void mergeDeviceData(Device device, DeviceRequestDto deviceRequestDto, Model newModel) {
        device.setModel(newModel);
        device.setName(deviceRequestDto.getName());
        device.setDescription(deviceRequestDto.getDescription());
        device.setDomainName(deviceRequestDto.getDomainName());
        device.setLogin(deviceRequestDto.getLogin());
        device.setPassword(deviceRequestDto.getPassword());
    }
}
