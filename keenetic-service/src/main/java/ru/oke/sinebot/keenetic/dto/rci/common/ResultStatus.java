package ru.oke.sinebot.keenetic.dto.rci.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Перечисление статусов выполнения управляющей операции над каким-либо объектом
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum ResultStatus {
    /*
    данный статус означает успех выполнения операции
     */
    MESSAGE("message"),

    /*
    данный статус означает провал выполнения операции
     */
    ERROR("error");

    @JsonValue
    private final String status;

    @JsonCreator
    public static ResultStatus from(final String status) {
        for (ResultStatus knownStatus : ResultStatus.values()) {
            if (knownStatus.status.equalsIgnoreCase(status)) {
                return knownStatus;
            }
        }
        return null;
    }
}
