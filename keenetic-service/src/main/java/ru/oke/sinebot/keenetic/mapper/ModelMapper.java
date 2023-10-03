package ru.oke.sinebot.keenetic.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.api.info.AccessPointResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.ActionDto;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceInfoDto;
import ru.oke.sinebot.keenetic.dto.api.info.ModelResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.VendorResponseDto;
import ru.oke.sinebot.keenetic.model.Model;
import ru.oke.sinebot.keenetic.model.Vendor;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ModelMapper {
    private final VendorMapper vendorMapper;

    private final AccessPointMapper accessPointMapper;

    private final ActionMapper actionMapper;

    public ModelResponseDto mapToModelResponseDto(Model model) {
        ModelResponseDto dto = new ModelResponseDto();
        dto.setId(model.getId());
        VendorResponseDto vendorDto = vendorMapper.mapToVendorResponseDto(model.getVendor());
        dto.setVendor(vendorDto);
        dto.setName(model.getName());
        List<AccessPointResponseDto> accessPointDtos = model.getAccessPoints().stream()
                .map(accessPointMapper::mapToResponseDto)
                .collect(Collectors.toList());
        dto.setAccessPoints(accessPointDtos);
        List<ActionDto> actionDtos = model.getSupportedActions().stream()
                .map(actionMapper::mapToActionDto)
                .collect(Collectors.toList());
        dto.setSupportedActions(actionDtos);
        return dto;
    }

    public DeviceInfoDto mapToDeviceInfoDto(Model model) {
        DeviceInfoDto dto = new DeviceInfoDto();
        Vendor modelVendor = model.getVendor();
        dto.setModelId(model.getId());
        dto.setVendor(modelVendor.getName());
        dto.setModel(model.getName());
        return dto;
    }
}
