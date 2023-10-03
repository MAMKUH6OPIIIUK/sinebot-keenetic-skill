package ru.oke.sinebot.yandex.dto.capability.mode;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.oke.sinebot.yandex.dto.capability.CapabilityParameters;

import java.util.List;

@AllArgsConstructor
@Data
public class ModeParameters implements CapabilityParameters {
    private ModeInstance instance;

    private List<Mode> modes;
}
