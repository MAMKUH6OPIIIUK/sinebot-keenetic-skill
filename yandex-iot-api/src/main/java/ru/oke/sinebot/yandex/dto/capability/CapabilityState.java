package ru.oke.sinebot.yandex.dto.capability;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.yandex.dto.capability.mode.ModeState;
import ru.oke.sinebot.yandex.dto.capability.onoff.OnOffState;

@Data
@NoArgsConstructor
public class CapabilityState {
    private CapabilityType type;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = OnOffState.class, name = "devices.capabilities.on_off"),
            @JsonSubTypes.Type(value = ModeState.class, name = "devices.capabilities.mode")
    })
    private State state;

    public CapabilityState(OnOffState state) {
        this.type = CapabilityType.ON_OFF;
        this.state = state;
    }

    public CapabilityState(ModeState state) {
        this.type = CapabilityType.MODE;
        this.state = state;
    }
}
