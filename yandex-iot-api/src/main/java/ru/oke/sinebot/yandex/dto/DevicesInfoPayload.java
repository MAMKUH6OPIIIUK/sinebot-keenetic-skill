package ru.oke.sinebot.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.yandex.dto.Device;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class DevicesInfoPayload {
    @JsonProperty("user_id")
    private String userId;

    private List<Device> devices;
}
