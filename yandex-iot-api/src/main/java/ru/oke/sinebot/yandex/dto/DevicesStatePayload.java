package ru.oke.sinebot.yandex.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class DevicesStatePayload {
    private List<DeviceState> devices;
}
