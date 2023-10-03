package ru.oke.sinebot.keenetic.converter;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.types.WifiFrequency;

@Component
public class WifiFrequencyConverter implements AttributeConverter<WifiFrequency, String> {
    @Override
    public String convertToDatabaseColumn(WifiFrequency frequency) {
        return frequency.getBand();
    }

    @Override
    public WifiFrequency convertToEntityAttribute(String dbData) {
        return WifiFrequency.from(dbData);
    }
}
