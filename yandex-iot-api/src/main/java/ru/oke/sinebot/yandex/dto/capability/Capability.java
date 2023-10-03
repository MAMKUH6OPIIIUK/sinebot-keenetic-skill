package ru.oke.sinebot.yandex.dto.capability;

import lombok.Data;
import ru.oke.sinebot.yandex.dto.capability.mode.ModeParameters;
import ru.oke.sinebot.yandex.dto.capability.onoff.OnOffParameters;

@Data
public class Capability {
    private CapabilityType type;

    private Boolean retrievable;

    private Boolean reportable;

    private CapabilityParameters parameters;

    public Capability(OnOffParameters parameters, Boolean retrievable) {
        this.type = CapabilityType.ON_OFF;
        this.retrievable = retrievable;
        this.reportable = false;
        this.parameters = parameters;
    }

    public Capability(ModeParameters parameters, Boolean retrievable) {
        this.type = CapabilityType.MODE;
        this.retrievable = retrievable;
        this.reportable = false;
        this.parameters = parameters;
    }
}
