package ru.oke.sinebot.keenetic.dto.api.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.keenetic.dto.types.PropertyType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyStatusRequestDto {
    private PropertyType type;
}
