package ru.oke.sinebot.yandex.dto.capability.onoff;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.yandex.dto.capability.State;

@Data
@NoArgsConstructor
public class OnOffState implements State {
    private OnOffInstance instance;

    private boolean value;

    public OnOffState(boolean value) {
        this.instance = OnOffInstance.ON;
        this.value = value;
    }
}
