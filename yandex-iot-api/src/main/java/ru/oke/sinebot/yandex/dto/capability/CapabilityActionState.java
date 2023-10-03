package ru.oke.sinebot.yandex.dto.capability;

import lombok.Data;
import ru.oke.sinebot.yandex.dto.capability.mode.ModeActionState;
import ru.oke.sinebot.yandex.dto.capability.onoff.OnOffActionState;

@Data
public class CapabilityActionState {
    private CapabilityType type;

    private ActionState state;

    public CapabilityActionState(OnOffActionState state) {
        this.type = CapabilityType.ON_OFF;
        this.state = state;
    }

    public CapabilityActionState(ModeActionState state) {
        this.type = CapabilityType.MODE;
        this.state = state;
    }
}
