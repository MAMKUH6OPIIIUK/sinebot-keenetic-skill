package ru.oke.sinebot.keenetic.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.api.info.AccessPointResponseDto;
import ru.oke.sinebot.keenetic.dto.api.info.PropertyResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointChangeStatusDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointStatusDto;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyChangeStatusResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyStatusResponseDto;
import ru.oke.sinebot.keenetic.model.wifi.AccessPoint;
import ru.oke.sinebot.keenetic.model.wifi.Property;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccessPointMapper {
    private final PropertyMapper propertyMapper;

    public AccessPointResponseDto mapToResponseDto(AccessPoint accessPoint) {
        AccessPointResponseDto dto = new AccessPointResponseDto();
        dto.setType(accessPoint.getType());
        dto.setBand(accessPoint.getBand());
        dto.setInterfaceName(accessPoint.getInterfaceName());
        List<PropertyResponseDto> propertyDtos = accessPoint.getProperties().stream()
                .map(propertyMapper::mapToPropertyResponseDto)
                .collect(Collectors.toList());
        dto.setProperties(propertyDtos);
        return dto;
    }

    public AccessPointStatusDto mapToStatusDto(AccessPoint accessPoint, String deviceDomainName) {
        AccessPointStatusDto dto = new AccessPointStatusDto();
        dto.setDomainName(deviceDomainName);
        dto.setInterfaceName(accessPoint.getInterfaceName());
        List<PropertyStatusRequestDto> propertyDtos = accessPoint.getProperties().stream()
                .filter(property -> property.getType() != null)
                .filter(Property::isRetrievable)
                .map(propertyMapper::mapToPropertyStatusRequestDto)
                .collect(Collectors.toList());
        dto.setProperties(propertyDtos);
        return dto;
    }

    public AccessPointStatusResponseDto mapToStatusResponseDto(AccessPoint accessPoint,
                                                               List<PropertyStatusResponseDto>
                                                                                  propertyStatuses) {
        AccessPointStatusResponseDto dto = new AccessPointStatusResponseDto();
        dto.setType(accessPoint.getType());
        dto.setBand(accessPoint.getBand());
        dto.setProperties(propertyStatuses);
        return dto;
    }

    public AccessPointChangeStatusDto mapToChangeStatusDto(AccessPointChangeStatusRequestDto requestedChangeDto,
                                                           AccessPoint accessPoint,
                                                           String deviceDomainName) {
        AccessPointChangeStatusDto dto = new AccessPointChangeStatusDto();
        dto.setDomainName(deviceDomainName);
        dto.setInterfaceName(accessPoint.getInterfaceName());
        dto.setType(accessPoint.getType());
        dto.setBand(accessPoint.getBand());
        dto.setProperties(requestedChangeDto.getProperties());
        return dto;
    }

    public AccessPointChangeStatusResponseDto mapToChangeStatusResponseDto(AccessPointChangeStatusDto changeStatusDto,
                                                                           List<PropertyChangeStatusResponseDto>
                                                                                   changePropertyStatuses) {
        AccessPointChangeStatusResponseDto dto = new AccessPointChangeStatusResponseDto();
        dto.setType(changeStatusDto.getType());
        dto.setBand(changeStatusDto.getBand());
        dto.setProperties(changePropertyStatuses);
        return dto;
    }
}
