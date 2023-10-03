package ru.oke.sinebot.yandex.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DeviceType {
    OTHER("devices.types.other");

    @JsonValue
    private final String type;
}
