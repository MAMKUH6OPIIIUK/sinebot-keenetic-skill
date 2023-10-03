package ru.oke.sinebot.keenetic.dto.api.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.keenetic.dto.types.PropertyType;

/**
 * Класс описывает объект, содержащий текущий статус определенного включаемого\выключаемого свойства точки доступа
 *
 * @author k.oshoev
 */
@NoArgsConstructor
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyStatusResponseDto {
    private PropertyType type;

    private Boolean enabled;

    private ErrorDto error;

    /**
     * Конструктор для создания dto с успешно запрошенным статусом свойства
     *
     * @param type    тип свойства
     * @param enabled включено или выключено свойство на устройстве
     */
    public PropertyStatusResponseDto(PropertyType type, boolean enabled) {
        this.type = type;
        this.enabled = enabled;
    }

    /**
     * Конструктор для создания dto, содержащего ошибку получения статуса конкретного свойства
     *
     * @param type  тип свойства
     * @param error информация о случившейся ошибке
     */
    public PropertyStatusResponseDto(PropertyType type, ErrorDto error) {
        this.type = type;
        this.error = error;
    }
}
