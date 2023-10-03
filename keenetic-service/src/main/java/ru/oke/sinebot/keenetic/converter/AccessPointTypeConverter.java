package ru.oke.sinebot.keenetic.converter;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.types.AccessPointType;

@Component
public class AccessPointTypeConverter implements AttributeConverter<AccessPointType, String> {
    @Override
    public String convertToDatabaseColumn(AccessPointType appType) {
        return appType.getType();
    }

    @Override
    public AccessPointType convertToEntityAttribute(String dbType) {
        return AccessPointType.from(dbType);
    }
}
