package ru.oke.sinebot.keenetic.dto.api.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.keenetic.dto.types.ActionType;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionResponseDto {
    private ActionType type;

    private boolean completed;

    private ErrorDto error;

    /**
     * Конструктор для генерации объектов, означающих успешное завершение действия
     *
     * @param type тип операции
     */
    public ActionResponseDto(ActionType type) {
        this.type = type;
        this.completed = true;
    }

    /**
     * Конструктор для объектов, означающих провал выполнения действия
     *
     * @param type  тип операции
     * @param error dto с описанием причины провала
     */
    public ActionResponseDto(ActionType type, ErrorDto error) {
        this.type = type;
        this.completed = false;
        this.error = error;
    }
}
