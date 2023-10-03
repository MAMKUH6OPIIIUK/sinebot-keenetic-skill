package ru.oke.sinebot.keenetic.dto.rci.operational.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.rci.common.ActionEntityStatusArray;

/**
 * Класс описывает объект, получаемый в качестве ответа на запрос перезагрузки роутера Keenetic
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SystemRebootResponse {

    private ActionEntityStatusArray reboot;
}
