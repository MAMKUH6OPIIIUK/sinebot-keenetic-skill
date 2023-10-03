package ru.oke.sinebot.keenetic.mapper;

import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.api.info.PropertyResponseDto;
import ru.oke.sinebot.keenetic.dto.api.status.PropertyStatusRequestDto;
import ru.oke.sinebot.keenetic.model.wifi.Property;

@Component
public class PropertyMapper {
    public PropertyResponseDto mapToPropertyResponseDto(Property property) {
        PropertyResponseDto dto = new PropertyResponseDto();
        dto.setType(property.getType());
        dto.setRetrievable(property.isRetrievable());
        return dto;
    }

    public PropertyStatusRequestDto mapToPropertyStatusRequestDto(Property property) {
        return new PropertyStatusRequestDto(property.getType());
    }
}
