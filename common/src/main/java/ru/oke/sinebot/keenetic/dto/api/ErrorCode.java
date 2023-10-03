package ru.oke.sinebot.keenetic.dto.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Известные типы кодов ошибок. Позаимствованы из API Яндекса, чтобы не делать маппинг своих внутренних ошибок на
 * ошибки платформы умного дома
 *
 * @author k.oshoev
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum ErrorCode {
    DEVICE_BUSY("DEVICE_BUSY"),
    DEVICE_NOT_FOUND("DEVICE_NOT_FOUND"),
    DEVICE_UNREACHABLE("DEVICE_UNREACHABLE"),
    INTERNAL_ERROR("INTERNAL_ERROR"),
    INVALID_ACTION("INVALID_ACTION"),
    NOT_SUPPORTED_IN_CURRENT_MODE("NOT_SUPPORTED_IN_CURRENT_MODE");

    @JsonValue
    private final String errorCode;

    @JsonCreator
    public ErrorCode from(String errorCode) {
        if (errorCode == null || errorCode.isEmpty()) {
            return null;
        }
        for (ErrorCode knownCode : ErrorCode.values()) {
            if (knownCode.errorCode.equalsIgnoreCase(errorCode)) {
                return knownCode;
            }
        }
        return ErrorCode.INTERNAL_ERROR;
    }
}
