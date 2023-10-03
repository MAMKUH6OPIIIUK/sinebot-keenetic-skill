package ru.oke.sinebot.keenetic.converter;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.types.ActionType;

@Component
public class ActionTypeConverter implements AttributeConverter<ActionType, String> {
    @Override
    public String convertToDatabaseColumn(ActionType attribute) {
        return attribute.getType();
    }

    @Override
    public ActionType convertToEntityAttribute(String dbData) {
        return ActionType.from(dbData);
    }
}
