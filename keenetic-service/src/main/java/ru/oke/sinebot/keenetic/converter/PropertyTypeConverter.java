package ru.oke.sinebot.keenetic.converter;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.types.PropertyType;

@Component
public class PropertyTypeConverter implements AttributeConverter<PropertyType, String> {
    @Override
    public String convertToDatabaseColumn(PropertyType appType) {
        return appType.getType();
    }

    @Override
    public PropertyType convertToEntityAttribute(String dbData) {
        return PropertyType.from(dbData);
    }
}
