package ru.oke.sinebot.keenetic.dto.api.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.keenetic.dto.types.ActionType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionDto {
    private ActionType type;
}
