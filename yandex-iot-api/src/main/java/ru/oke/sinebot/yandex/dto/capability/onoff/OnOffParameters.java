package ru.oke.sinebot.yandex.dto.capability.onoff;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.oke.sinebot.yandex.dto.capability.CapabilityParameters;

@AllArgsConstructor
@Data
public class OnOffParameters implements CapabilityParameters {
    private Boolean split;
}
