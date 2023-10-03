package ru.oke.sinebot.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.oke.sinebot.yandex.dto.capability.Capability;

import java.util.List;

@Data
public class Device {
    private String id;

    private String name;

    private String description;

    private DeviceType type;

    private List<Capability> capabilities;

    @JsonProperty("device_info")
    private DeviceInfo deviceInfo;

    public Device() {
        this.type = DeviceType.OTHER;
    }

    public Device(String id, String name, String description, List<Capability> capabilities, DeviceInfo deviceInfo) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.capabilities = capabilities;
        this.deviceInfo = deviceInfo;
        this.type = DeviceType.OTHER;
    }
}
