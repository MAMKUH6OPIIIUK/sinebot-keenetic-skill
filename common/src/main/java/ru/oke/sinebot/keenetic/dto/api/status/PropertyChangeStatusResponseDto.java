package ru.oke.sinebot.keenetic.dto.api.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.keenetic.dto.types.PropertyType;

/**
 * Класс описывает объект с результатом изменения статуса какого-либо свойства устройств
 *
 * @author k.oshoev
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyChangeStatusResponseDto {
    private PropertyType type;

    private Boolean changed;

    private Boolean configChanged;

    private ErrorDto error;

    /**
     * Конструктор для создания объекта, означающего успешную операцию над свойством определенного типа
     *
     * @param type    тип свойства
     * @param changed было ли изменено свойство, либо это не потребовалось
     */
    public PropertyChangeStatusResponseDto(PropertyType type, boolean changed) {
        this.type = type;
        this.changed = changed;
        this.configChanged = changed;
    }

    /**
     * Конструктор для создания объекта, означающего успешную операцию над свойством определенного типа
     *
     * @param type          тип свойства
     * @param changed       было ли изменено свойство, либо это не потребовалось
     * @param configChanged было ли выполнено изменение конфигурации (можно использовать для принятия решения о
     *                      необходимости сохранения или отката конфигурации)
     */
    public PropertyChangeStatusResponseDto(PropertyType type, boolean changed, boolean configChanged) {
        this.type = type;
        this.changed = changed;
        this.configChanged = configChanged;
    }

    /**
     * Конструктор для создания объекта, означающего провал изменения свойства определенного типа
     *
     * @param type  тип свойства
     * @param error ошибка, из-за которой не удалось выполнить изменение
     */
    public PropertyChangeStatusResponseDto(PropertyType type, ErrorDto error) {
        this.type = type;
        this.changed = false;
        this.configChanged = false;
        this.error = error;
    }
}
