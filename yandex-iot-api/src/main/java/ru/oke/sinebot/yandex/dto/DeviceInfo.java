package ru.oke.sinebot.yandex.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceInfo {

    private String manufacturer;

    private String model;

    @JsonProperty("hw_version")
    private String hwVersion;

    @JsonProperty("sw_version")
    private String swVersion;

    public DeviceInfo(String manufacturer, String model) {
        this.manufacturer = manufacturer;
        this.model = model;
    }

}
