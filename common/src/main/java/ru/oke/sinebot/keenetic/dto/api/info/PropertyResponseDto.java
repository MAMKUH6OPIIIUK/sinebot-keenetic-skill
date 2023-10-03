package ru.oke.sinebot.keenetic.dto.api.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.keenetic.dto.types.PropertyType;

/**
 * Класс описывает DTO с управляемым свойством точки доступа
 *
 * @author k.oshoev
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyResponseDto {
    private PropertyType type;

    private boolean retrievable;
}
