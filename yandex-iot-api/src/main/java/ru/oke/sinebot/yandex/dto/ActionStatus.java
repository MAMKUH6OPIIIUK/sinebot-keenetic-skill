package ru.oke.sinebot.yandex.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ActionStatus {
    DONE("DONE"),
    ERROR("ERROR");

    @JsonValue
    private final String status;
}
