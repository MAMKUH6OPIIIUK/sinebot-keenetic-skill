package ru.oke.sinebot.yandex.dto.capability.mode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.yandex.dto.capability.State;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModeState implements State {
    private ModeInstance instance;

    private ModeValue value;
}
